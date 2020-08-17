package com.slk.resource.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


@Configuration
@EnableResourceServer
@EnableConfigurationProperties(AuthorizationServerProperties.class)
public class OauthResourceConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private ResourceServerProperties resourceServerProperties;




    //与授权服务器使用共同的密钥进行解析
/*    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }*/
    @Bean
    public AccessTokenConverter accessTokenConverter() {
        return new DefaultAccessTokenConverter();
    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
//                .authenticationEntryPoint((request, response, authException) -> {})
                .and()
                .authorizeRequests()
                .antMatchers("/actuator","/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();

    }

    /**
     * 资源服务器通过访问授权服务器 /oauth/check_token 端点解析令牌需要使用 RemoteTokenServices
     *
     * @param oAuth2ClientProperties
     * @param authorizationServerProperties
     * @param jwtAccessTokenConverter
     * @return
     */
    @Bean
    public ResourceServerTokenServices tokenServices(OAuth2ClientProperties oAuth2ClientProperties
            , AuthorizationServerProperties authorizationServerProperties
            , JwtAccessTokenConverter jwtAccessTokenConverter) {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl(authorizationServerProperties.getCheckTokenAccess());
        remoteTokenServices.setClientId(oAuth2ClientProperties.getClientId());
        remoteTokenServices.setClientSecret(oAuth2ClientProperties.getClientSecret());
        remoteTokenServices.setAccessTokenConverter(jwtAccessTokenConverter);
        return remoteTokenServices;
    }

}
