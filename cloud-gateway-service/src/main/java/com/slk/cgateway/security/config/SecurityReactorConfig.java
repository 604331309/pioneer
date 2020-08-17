package com.slk.cgateway.security.config;

import com.slk.cgateway.security.converter.JWTAuthenticationConverter;
import com.slk.cgateway.security.matcher.JWTAuthenticationServerWebExchangeMatcher;
import com.slk.cgateway.security.matcher.JWTAuthentizationServerWebExchangeMatcher;
import com.slk.cgateway.utils.jwt.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityReactorConfig {
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Bean
    public JWTAuthenticationConverter jwtAuthenticationConverter() {
        return new JWTAuthenticationConverter(jwtTokenUtils);
    }

    @Bean
    public JWTAuthenticationServerWebExchangeMatcher jwtBearerTokenServerWebExchangeMatcher() {
        JWTAuthenticationServerWebExchangeMatcher matcher = new JWTAuthenticationServerWebExchangeMatcher();
        matcher.setJWTBearerTokenServerWebExchangeMatcher(jwtAuthenticationConverter());
        return matcher;
    }

    @Bean
    public JWTAuthentizationServerWebExchangeMatcher jwtAuthentizationServerWebExchangeMatcher() {
        JWTAuthentizationServerWebExchangeMatcher matcher = new JWTAuthentizationServerWebExchangeMatcher();
        matcher.setJWTBearerTokenServerWebExchangeMatcher(jwtAuthenticationConverter());
        return matcher;
    }
}
