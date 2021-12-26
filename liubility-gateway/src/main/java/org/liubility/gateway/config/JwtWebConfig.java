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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String path = request.getPath().value();
        boolean valid = jwtProperty.getIgnore().stream().anyMatch(item -> antPathMatcher.match(item, path));
        if (valid) {
            return webFilterChain.filter(serverWebExchange);
        }

        ServerHttpResponse response = serverWebExchange.getResponse();
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return this.setErrorResponse(response, Result.authFail("未携带token"));
        }

        String token = authorization.substring(7);
        try {
            if (jwtService.isTokenExpired(token)) {
                return this.setErrorResponse(response, Result.authFail("token已过期"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return this.setErrorResponse(response, Result.error("token解析异常"));
        }

        try {
            AccountDto accountDto = jwtService.getSubjectFromToken(token,AccountDto.class);
            ServerHttpRequest mutateReq = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("username", accountDto.getUsername());
                httpHeaders.add("userId", String.valueOf(accountDto.getId()));
                httpHeaders.set(HttpHeaders.AUTHORIZATION, token);
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
        return response.writeWith(Mono.just(response.bufferFactory().wrap(JsonUtils.object2Byte(object))));
    }


    /**
     * 获取请求体中的字符串内容
     * @param serverHttpRequest
     * @return
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest){
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
}