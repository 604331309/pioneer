package com.slk.cgateway.utils.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chunliucq
 * @since 11/09/2019 20:16
 */
@Configuration
@ConfigurationProperties("security.jwt")
@Data
public class JwtProperties {
    /**
     * JWT 密码
     */
    private String secret;
    /**
     * JWT请求  header名称
     */
    private String header = "Authorization";
    /**
     * AUTH TOKEN 前缀
     */
    private String tokenHead = "Bearer ";
    /**
     * 有效期
     */
    private int expiration = 10000000;
}
