//package org.liubility.gateway.security;
//
//import org.springframework.security.authorization.AuthorityReactiveAuthorizationManager;
//import org.springframework.security.authorization.ReactiveAuthorizationManager;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.web.server.authorization.AuthorizationContext;
//import org.springframework.security.web.server.authorization.DelegatingReactiveAuthorizationManager;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcherEntry;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
///**
// * @Author JDragon
// * @Date 2021.02.10 下午 8:59
// * @Email 1061917196@qq.com
// * url动态拦截鉴权
// */
//
//@Component
//public class AccessWebFilter implements WebFilter {
//
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        String path = exchange.getRequest().getPath().pathWithinApplication().value();
//
//        ServerWebExchangeMatchers.pathMatchers(IgnoreAuthWhitelist.AUTH_WHITELIST)
//                .matches(exchange)
//                .map(ServerWebExchangeMatcher.MatchResult::isMatch)
//                .subscribe(IgnoreAuthWhitelist::set);
//
//        //排除.pathMatchers(AUTH_WHITELIST).permitAll()
//        if (IgnoreAuthWhitelist.get()) {
//            return chain.filter(exchange);
//        }
//
//        DelegatingReactiveAuthorizationManager.Builder builder = DelegatingReactiveAuthorizationManager.builder();
//
//        AuthorityReactiveAuthorizationManager<AuthorizationContext> authorityManager = AuthorityReactiveAuthorizationManager.hasAuthority(path);
//
//        //参考 org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec.Access
//        builder.add(new ServerWebExchangeMatcherEntry<>(
//                ServerWebExchangeMatchers.pathMatchers(path), authorityManager));
//
//        DelegatingReactiveAuthorizationManager manager = builder.build();
//
//        //参考 org.springframework.security.web.server.authorization.AuthorizationWebFilter.filter
//        return ReactiveSecurityContextHolder.getContext()
//                .filter(c -> c.getAuthentication() != null)
//                .map(SecurityContext::getAuthentication)
//                .as(authentication -> manager.verify(authentication, exchange))
//                .switchIfEmpty(chain.filter(exchange));
//    }
//}
