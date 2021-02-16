//package org.liubility.gateway.security;
//
//import com.alibaba.cloud.commons.lang.StringUtils;
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.web.server.context.ServerSecurityContextRepository;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @Author JDragon
// * @Date 2021.02.10 下午 8:57
// * @Email 1061917196@qq.com
// * 认证缓存(token缓存)
// */
//
//
//@Component
//public class SecurityContextRepository implements ServerSecurityContextRepository {
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Override
//    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
//        if (context.getAuthentication() instanceof TokenAuthentication) {
//            TokenAuthentication authentication = (TokenAuthentication) context.getAuthentication();
//            redisTemplate.opsForValue().set(authentication.getToken(), JSONObject.toJSONString(context.getAuthentication()));
//        }
//        return Mono.empty();
//    }
//
//    /**
//     * debug 没有进入此方法过
//     *
//     * @author sunmj
//     * @date 2019/11/27
//     */
//    @Override
//    public Mono<SecurityContext> load(ServerWebExchange exchange) {
//        ServerHttpRequest request = exchange.getRequest();
//        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (StringUtils.isEmpty(authorization)) {
//            return Mono.empty();
//        }
//        //是否只保证一个token有效
//        return Mono.just(JSONObject.parseObject(String.valueOf(redisTemplate.opsForValue().get(authorization)), SecurityContext.class));
//    }
//}
