//package org.liubility.gateway.security;
//
//import com.alibaba.cloud.commons.lang.StringUtils;
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @Author JDragon
// * @Date 2021.02.10 下午 8:55
// * @Email 1061917196@qq.com
// * 认证具体实现(判断token真实性)
// */
//
//@Component
//public class AuthenticationConverter implements ServerAuthenticationConverter {
//
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    private static final String BEARER = "Bearer ";
//
//    @Override
//    public Mono<Authentication> convert(ServerWebExchange exchange) {
//        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
//        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (StringUtils.isEmpty(token)) {
//            return Mono.just(anonymous);
//        }
//        if (!token.startsWith(BEARER) || token.length() <= BEARER.length() /*|| !tokenCache.containsKey(token.substring(BEARER.length()))*/) {
//            return Mono.just(anonymous);
//        }
//
//        String authenticationStr = (String) redisTemplate.opsForValue().get(token.substring(BEARER.length()));
//        if (StringUtils.isEmpty(authenticationStr)) {
//            return Mono.just(anonymous);
//        }
//        UsernamePasswordAuthenticationToken authentication = JSONObject.parseObject(authenticationStr, UsernamePasswordAuthenticationToken.class);
//
//        return Mono.just(authentication);
//    }
//}
