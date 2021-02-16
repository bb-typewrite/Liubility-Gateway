//package org.liubility.gateway.security;
//
//import org.liubility.commons.http.response.normal.Result;
//import org.liubility.commons.json.JsonUtils;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.Charset;
//
///**
// * @Author JDragon
// * @Date 2021.02.10 下午 8:41
// * @Email 1061917196@qq.com
// */
//
//@Component
//public class RequestDeniedHandler implements ServerAccessDeniedHandler {
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
//        return Mono.defer(() -> Mono.just(exchange.getResponse()))
//                .flatMap(response -> {
//                    response.setStatusCode(HttpStatus.FORBIDDEN);
//                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//                    DataBufferFactory dataBufferFactory = response.bufferFactory();
//                    Result<String> result = Result.authFail("权限不足");
//                    DataBuffer buffer = dataBufferFactory.wrap(JsonUtils.object2Byte(result));
//                    return response.writeWith(Mono.just(buffer))
//                            .doOnError(error -> DataBufferUtils.release(buffer));
//                });
//    }
//}
