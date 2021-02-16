//package org.liubility.gateway.security;
//
//import org.liubility.commons.http.response.normal.Result;
//import org.liubility.commons.json.JsonUtils;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.Charset;
//
///**
// * @Author JDragon
// * @Date 2021.02.10 下午 8:43
// * @Email 1061917196@qq.com
// */
//
//@Component
//public class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
//    @Override
//    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
//        return Mono.defer(() -> Mono.just(exchange.getResponse()))
//                .flatMap(response -> {
//                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
//                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//                    DataBufferFactory dataBufferFactory = response.bufferFactory();
//                    Result<String> result = Result.authFail("需要认证");
//                    DataBuffer buffer = dataBufferFactory.wrap(JsonUtils.object2Byte(result));
//                    return response.writeWith(Mono.just(buffer))
//                            .doOnError(error -> DataBufferUtils.release(buffer));
//                });
//    }
//}
