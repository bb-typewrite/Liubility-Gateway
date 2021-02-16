//package org.liubility.gateway.security;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
//import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
//import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
//import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
//import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
//import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
//import org.springframework.security.web.server.context.ServerSecurityContextRepository;
//import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
//import org.springframework.web.server.WebFilter;
//import reactor.core.publisher.Mono;
//
//import javax.annotation.Resource;
//import java.util.LinkedList;
//
///**
// * @Author JDragon
// * @Date 2021.02.10 下午 8:37
// * @Email 1061917196@qq.com
// */
//
//@Slf4j
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private ServerAccessDeniedHandler serverAccessDeniedHandler;
//
//    @Autowired
//    private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint;
//
//    @Resource
//    private WebFilter accessWebFilter;
//
//    @Autowired
//    private ReactiveUserDetailsService userServiceImpl;
//
//    /**
//     * security 配置
//     * @param http
//     * @return
//     */
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf().disable()
//                .formLogin().disable()
//                .httpBasic().disable()
//                .exceptionHandling()
//                .accessDeniedHandler(serverAccessDeniedHandler)
//                .authenticationEntryPoint(serverAuthenticationEntryPoint)//处理认证异常
//                .and()
//                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)//认证
//                .addFilterAt(accessWebFilter, SecurityWebFiltersOrder.AUTHORIZATION)//动态鉴权
//                .authorizeExchange()
//                .pathMatchers(IgnoreAuthWhitelist.AUTH_WHITELIST).permitAll()//不需要认证
//                .anyExchange().authenticated()//其他所有的都要认证
//                .and().build();
//    }
//
//    @Autowired
//    private ServerAuthenticationConverter serverAuthenticationConverter;
//
//    @Autowired
//    private ServerSecurityContextRepository serverSecurityContextRepository;
//
//    @Autowired
//    private JwtManager jwtManager;
//    /**
//     * 认证
//     *
//     * @author sunmj
//     * @date 2019/11/19
//     */
//    @Bean
//    AuthenticationWebFilter authenticationWebFilter() {
////        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveAuthenticationManager());
//
//        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtManager);
//
//        ServerWebExchangeMatcher serverWebExchangeMatcher = ServerWebExchangeMatchers.pathMatchers(IgnoreAuthWhitelist.AUTH_WHITELIST);
//        NegatedServerWebExchangeMatcher negateWhiteList = new NegatedServerWebExchangeMatcher(serverWebExchangeMatcher);
//
//        authenticationWebFilter.setRequiresAuthenticationMatcher(negateWhiteList);
//        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter);
//        authenticationWebFilter.setSecurityContextRepository(serverSecurityContextRepository);
//        authenticationWebFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(serverAuthenticationEntryPoint));
//        return authenticationWebFilter;
//    }
////
//}
