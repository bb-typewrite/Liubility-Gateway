package org.liubility.gateway.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @Author JDragon
 * @Date 2021.02.10 下午 10:48
 * @Email 1061917196@qq.com
 * @Des:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {

    private String username;

    private String password;
}
