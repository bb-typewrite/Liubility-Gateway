package org.liubility.gateway.filter;

import org.liubility.api.test.TestFeignServer;
import org.liubility.commons.http.response.normal.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @Author: Jdragon
 * @Class: AuthorizeFilter
 * @Date: 2021.02.06 上午 2:13
 * @Description:
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    @Autowired
    private TestFeignServer testFeignServer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //不合法(响应未登录的异常)
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        Result<String> result = testFeignServer.test();
        String message = result.getMessage();
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(message.getBytes());
        return response.writeWith(Mono.just(bodyDataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
