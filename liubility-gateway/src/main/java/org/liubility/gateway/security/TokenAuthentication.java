//package org.liubility.gateway.security;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//
//import java.util.Collection;
//
///**
// * @Author JDragon
// * @Date 2021.02.10 下午 11:33
// * @Email 1061917196@qq.com
// * @Des:
// */
//
//public class TokenAuthentication extends UsernamePasswordAuthenticationToken {
//
//    private final String token;
//
//    public TokenAuthentication(String token, Object principal, Object credentials) {
//        super(principal, credentials);
//        this.token = token;
//    }
//
//    public TokenAuthentication(String token, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
//        super(principal, credentials, authorities);
//        this.token = token;
//    }
//
//    public String getToken() {
//        return token;
//    }
//}