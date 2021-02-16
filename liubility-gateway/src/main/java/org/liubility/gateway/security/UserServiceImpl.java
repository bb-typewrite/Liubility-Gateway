//package org.liubility.gateway.security;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * @Author JDragon
// * @Date 2021.02.10 下午 8:48
// * @Email 1061917196@qq.com
// * UserDetailService动态获取
// */
//@Slf4j
//@Service
//public class UserServiceImpl implements ReactiveUserDetailsService {
//
//
//    public static final String PASSWD = "weiyan!";//固定密码
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Override
//    public Mono<UserDetails> findByUsername(String username) {
//        /*
//				正常步骤 根据username从数据库查询对应的user
//				因为这边是给ucenter-web认证 只有正确的情况才会存储认证信息所以可以不校验密码信息(这里使用固定密码)
//			 */
//        List<String> strings = Arrays.asList("/loginRob", "/guoji", "/ucenter-web/oneself/permissions");
//        //redis 获取用户所有的权限
//        List<String> range = redisTemplate.opsForList().range(username, 0, -1);//redis序列化的原因 误认为是final修饰的问题
//        range.addAll(strings);
//
//        String[] authorityArray = range.toArray(new String[]{});
//        log.info(" username : {} ", username);
//
//        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        User.UserBuilder userBuilder = User.builder();
//        UserDetails result = userBuilder.username(username).password(PASSWD).authorities(authorityArray).passwordEncoder(encoder::encode).build();
//        return result == null ? Mono.empty() : Mono.just(User.withUserDetails(result).build());
//    }
//
//}
