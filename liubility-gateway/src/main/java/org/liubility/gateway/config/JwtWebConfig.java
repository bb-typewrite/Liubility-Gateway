package org.liubility.gateway.config;

import org.liubility.api.AccountServiceProvider;
import org.liubility.commons.jwt.JwtServiceImpl;
import org.liubility.commons.dto.account.AccountDto;
import org.liubility.commons.http.response.normal.Result;
import org.liubility.commons.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author: Jdragon
 * @Class: JwtWebConfig
 * @Date: 2021.02.08 下午 12:23
 * @Description:
 */
@Configuration
public class JwtWebConfig implements WebFilter {

    @Autowired
    private JwtServiceImpl jwtService;

    @Autowired
    private AccountServiceProvider accountServiceProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        if (request.getPath().value().contains("/account/login")) {
            return webFilterChain.filter(serverWebExchange);
        }

        ServerHttpResponse response = serverWebExchange.getResponse();
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return this.setErrorResponse(response, "未携带token");
        }
        String token = authorization.substring(7);
        if(!jwtService.isTokenExpired(token)){
            return this.setErrorResponse(response,"token已过期");
        }
        try {
            String username = jwtService.getUsernameFromToken(token);
            AccountDto loginAccountByName = accountServiceProvider.getLoginAccountByName(username);
            if(loginAccountByName == null){
                return this.setErrorResponse(response,"token异常");
            }
            System.out.println(loginAccountByName);
        } catch (Exception e) {
            return this.setErrorResponse(response, Result.error(e.getMessage()));
        }
        return webFilterChain.filter(serverWebExchange);

    }

    protected Mono<Void> setErrorResponse(ServerHttpResponse response, Object object) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(JsonUtils.object2Byte(object))));

    }
}