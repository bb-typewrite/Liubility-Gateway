//package org.liubility.gateway.security;
//
///**
// * @Author JDragon
// * @Date 2021.02.10 下午 9:04
// * @Email 1061917196@qq.com
// * 是否需要认证
// */
//public class IgnoreAuthWhitelist {
//
//    public static final String[] AUTH_WHITELIST = new String[]{"/login", "/actuator/**", "/ucenter-web/login"};
//
//    private static ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();
//
//    private IgnoreAuthWhitelist() {
//    }
//
//    public static void set(boolean auth) {
//        threadLocal.set(auth);
//    }
//
//    public static boolean get() {
//        return threadLocal.get();
//    }
//}
