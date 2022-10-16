package org.liubility.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.liubility.api.AccountServiceProvider;
import org.liubility.commons.jwt.JwtProperty;
import org.liubility.commons.jwt.JwtServiceImpl;
import org.liubility.commons.dto.account.AccountDto;
import org.liubility.commons.http.response.normal.Result;
import org.liubility.commons.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author: Jdragon
 * @Class: JwtWebConfig
 * @Date: 2021.02.08 下午 12:23
 * @Description:
 */
@Configuration
@Slf4j
public class JwtWebConfig implements WebFilter {

    @Autowired
    private JwtProperty jwtProperty;

    @Autowired
    private JwtServiceImpl jwtService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpResponse response = serverWebExchange.getResponse();

        InetSocketAddress remoteAddress = serverWebExchange.getRequest().getRemoteAddress();
        if (remoteAddress == null) {
            log.error("请求的remoteAddress为空，不放行");
            return this.setErrorResponse(response, Result.error("请求异常"));
        }
//        String hostAddress = remoteAddress.getAddress().getHostAddress();
        String hostAddress = getIpAddress(serverWebExchange.getRequest());
        String requestPath = serverWebExchange.getRequest().getPath().value();
        log.info("来自{}的请求{}", hostAddress, requestPath);
        ServerHttpRequest request = serverWebExchange.getRequest();

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String path = request.getPath().value();
        boolean valid = jwtProperty.getIgnore().stream().anyMatch(item -> antPathMatcher.match(item, path));
        if (valid) {
            ServerHttpRequest mutateReq = request.mutate().headers(httpHeaders -> httpHeaders.add("ip", hostAddress)).build();
            return webFilterChain.filter(serverWebExchange.mutate().request(mutateReq).build());
        }

        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS)) {
            return webFilterChain.filter(serverWebExchange);
        }

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return this.setErrorResponse(response, Result.authFail("未携带token"));
        }

        String token = authorization.substring(7);
        AccountDto accountDto = jwtService.getSubjectFromToken(token, AccountDto.class);
        try {
//            if (jwtService.isTokenExpired(token)) {
//                return this.setErrorResponse(response, Result.authFail("token已过期"));
//            }
            Boolean member = stringRedisTemplate.opsForSet().isMember("lb:allow-ips:" + accountDto.getId(), hostAddress);
            if (member == null || !member) {
//            if (!Objects.equals(accountDto.getIp(), hostAddress)) {
                log.error("token异常：{}：{}", accountDto.getIp(), hostAddress);
                return this.setErrorResponse(response, Result.authFail("网络环境异常"));
            }

            String lastToken = stringRedisTemplate.opsForValue().get("lb:token:" + accountDto.getId());
            if (!Objects.equals(lastToken, token)) {
                log.error("token已过期：{},new token:{}", token, lastToken);
                return this.setErrorResponse(response, Result.authFail("token已过期"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return this.setErrorResponse(response, Result.authFail("token解析异常"));
        }

        try {
            ServerHttpRequest mutateReq = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("username", accountDto.getUsername());
                httpHeaders.add("userId", String.valueOf(accountDto.getId()));
                httpHeaders.add("ip", hostAddress);
                httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
            }).build();
            return webFilterChain.filter(serverWebExchange.mutate().request(mutateReq).build());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return this.setErrorResponse(response, Result.unKnowError("系统繁忙"));
        }
    }

    protected Mono<Void> setErrorResponse(ServerHttpResponse response, Object object) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        corsWebFilter(response);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(JsonUtils.object2Byte(object))));
    }

    private static final String UNKNOWN = "unknown";

    private String getIpAddress(ServerHttpRequest request) {
        org.springframework.http.HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("X-Real-IP");
        if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("x-forwarded-for");
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = Optional.ofNullable(request.getRemoteAddress())
                    .map(address -> address.getAddress().getHostAddress())
                    .orElse("");
            if ("127.0.0.1".equals(ip)) {
                // 根据网卡取本机配置的IP
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ip = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    // ignore
                }
            }
        }
        return ip;
    }

    /**
     * 获取请求体中的字符串内容
     *
     * @param serverHttpRequest
     * @return
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        return sb.toString();

    }

    private void corsWebFilter(ServerHttpResponse response) {
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Methods", "*");
        response.getHeaders().add("Access-Control-Allow-Headers", "*");
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION);
    }
}