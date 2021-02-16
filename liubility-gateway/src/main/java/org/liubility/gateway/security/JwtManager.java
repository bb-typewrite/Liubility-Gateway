//package org.liubility.gateway.security;
//
//import io.jsonwebtoken.Claims;
//import org.liubility.api.AccountServiceProvider;
//import org.liubility.commons.dto.account.AccountDto;
//import org.liubility.gateway.config.JwtServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @Author JDragon
// * @Date 2021.02.12 下午 4:28
// * @Email 1061917196@qq.com
// * @Des:
// */
//
//@Component
//public class JwtManager implements ReactiveAuthenticationManager {
//
//    @Autowired
//    private ReactiveUserDetailsService userServiceImpl;
//
//    @Autowired
//    private JwtServiceImpl jwtService;
//
//    @Autowired
//    private AccountServiceProvider accountServiceProvider;
//
//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) {
//        String name = authentication.getPrincipal().toString();
//        String password = authentication.getCredentials().toString();
////        AccountDto loginAccountByName = accountServiceProvider.getLoginAccountByName(name);
//        AccountDto loginAccount = new AccountDto();
//
//        try {
//            //todo 此处应该列出token中携带的角色表。
//            List<String> roles = new ArrayList<>();
//            roles.add("user");
//            Authentication authentication1 = new UsernamePasswordAuthenticationToken(
//                    name,
//                    password,
//                    roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
//            );
//            return Mono.just(authentication1);
//        } catch (Exception e) {
//            throw new BadCredentialsException(e.getMessage());
//        }
//    }
//}
