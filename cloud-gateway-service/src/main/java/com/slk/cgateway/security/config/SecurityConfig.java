package com.slk.cgateway.security.config;

import com.slk.cgateway.security.manager.CustomAuthenticationManager;
import com.slk.cgateway.security.matcher.JWTAuthentizationServerWebExchangeMatcher;
import com.slk.cgateway.security.repository.JWTSecurityContextRepository;
import com.slk.cgateway.security.converter.JWTAuthenticationConverter;
import com.slk.cgateway.security.handler.AuthenticationFaillHandler;
import com.slk.cgateway.security.handler.JWTAuthenticationSuccessHandler;
import com.slk.cgateway.security.handler.CustomServerAuthenticationEntryPoint;
import com.slk.cgateway.security.matcher.JWTAuthenticationServerWebExchangeMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    private JWTAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFaillHandler jwtAuthenticationFaillHandler;
    @Autowired
    private CustomServerAuthenticationEntryPoint customHttpBasicServerAuthenticationEntryPoint;
    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;
    @Autowired
    private JWTSecurityContextRepository jwtSecurityContextRepository;




    @Autowired
    JWTAuthenticationServerWebExchangeMatcher jwtAuthenticationServerWebExchangeMatcher;
    @Autowired
    JWTAuthentizationServerWebExchangeMatcher jwtAuthentizationServerWebExchangeMatcher;
    @Autowired
    JWTAuthenticationConverter jwtAuthenticationConverter;
    //    @Autowired
//    private JWTReactiveAuthenticationManager jwtAuthenticationManager;
    //security的鉴权排除列表
    private static final String[] excludedAuthPages = {
            "/auth/login",
            "/auth/logout",
            "/health",
            "/api/socket/**",
            "/docker/**"
    };

  /*  @Bean
    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin()
                .authorizeExchange()
                .pathMatchers(excludedAuthPages).permitAll()  //无需进行权限过滤的请求路径
                .pathMatchers(HttpMethod.OPTIONS).permitAll() //option 请求默认放行
                .anyExchange().authenticated()
                .and()
                .httpBasic().disable()
//                .formLogin().loginPage("/auth/login")
                .formLogin()
                .authenticationSuccessHandler(authenticationSuccessHandler) //认证成功
                .authenticationFailureHandler(authenticationFaillHandler) //登陆验证失败
                .and().exceptionHandling().authenticationEntryPoint(customHttpBasicServerAuthenticationEntryPoint)  //基于http的接口请求鉴权失败
                .and() .csrf().disable()//必须支持跨域
                .logout().disable();

        return http.build();
    }*/

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable();
        http
                .exceptionHandling()
                //鉴权失败时调用，这个可以看作是全局失败时的handler，就是说如果自定义的filter没有指定faillHandler，那么就会默认执行这个handler
                .authenticationEntryPoint(customHttpBasicServerAuthenticationEntryPoint)
                .and()
                .authorizeExchange()
                .and()
                .authorizeExchange()
                .and()
//                .addFilterAt(webFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                // SecurityWebFiltersOrder仅仅只是一个执行时机不要被枚举项的意思给迷惑，内置的枚举顺序代表了当前的filter在过滤链上的执行顺序
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)//认证
                .authorizeExchange()
//                .pathMatchers("/auth/login").permitAll()
                .pathMatchers("/**").authenticated()
                .and()
                .addFilterAt(authentzationWebFilter(),SecurityWebFiltersOrder.AUTHORIZATION)//授权
                .authorizeExchange()
                .anyExchange()
                .authenticated();

        return http.build();
    }

    /**
     * 以下是针对登录认证的filter配置，本质上不同的认证方式就是配置不同的AuthenticationWebFilter<br/>
     * {@link AuthenticationWebFilter}默认进行了很多内置的配置，例如默认的convert，默认的successHandler等，以下描述了大致的认证流程
     * <li>1.一个请求进来首先执行{@link AuthenticationWebFilter#filter}</li>
     * <li>2.{@link AuthenticationWebFilter#filter}中会去执行{@link ServerWebExchangeMatcher#matches}
     *       用于匹配任意条件，条件成立返回true，如果匹配器为false，则不会调用后续步骤并直接进入到{@link ServerAuthenticationEntryPoint#commence}，但是会继续调用后续的过滤器链，
     *       针对这一特性可以同时实现多种认证模式，例如常规用户名密码登陆并且同时可以具备短信登录的功能。
     *       e.g.可以参考官方实现的{@link PathPatternParserServerWebExchangeMatcher}，其匹配一个指定url 例如/oauth/login，
     *       实现该接口可以自定义匹配器规则,例如只匹配请求header中带了user=1的请求</li>
     * <li>3.然后会执行{@link ServerAuthenticationConverter#convert}用于将请求中的特定认证标识（例如用户名密码，或者token，或者短信code）转换为{@link Authentication},
     *       这里是可以自己写实现，例如通过basic获取或是jwt机制获取亦或是短信方式  </li>
     * <li>4.然后会进入{@link ReactiveAuthenticationManager}执行authenticate方法将步骤3中转换的{@link Authentication}用于验证，
     *       可参考{@link UserDetailsRepositoryReactiveAuthenticationManager}该类的authenticate方法用于将上一步（coverter）生成的Authentication
     *       进行验证用户合法性，如果合法则生成一个新的{@link Authentication}同时需调用Authentication的构造方法以便将其内部的authenticated变量变为true，这里很关键，不然后续就不能通过验证</li>
     * <li>5.程序继续往后，验证成功则会通用{@link AuthenticationWebFilter#onAuthenticationSuccess}调用{@link WebFilterChainServerAuthenticationSuccessHandler},
     *       同时会调用securityContextRepository.save存储上下文，否则调用{@link ServerAuthenticationFailureHandler}</li>
     *
     *<li>{@link AuthenticationWebFilter#setSecurityContextRepository}此处设置的是最终认证结束后的{@link SecurityContext}对象以何种策略存储，默认是session机制，
     *       如果自己实现的jwt，可以在{@link ServerSecurityContextRepository}save方法返回一个空的Mono，load方法中直接通过请求的jwt做解析
     *       可以参考这里实现的{@link JWTSecurityContextRepository}</li>
     */
    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(customAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter);
        authenticationWebFilter.setRequiresAuthenticationMatcher(jwtAuthenticationServerWebExchangeMatcher);
        authenticationWebFilter.setSecurityContextRepository(jwtSecurityContextRepository);
        authenticationWebFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
        authenticationWebFilter.setAuthenticationFailureHandler(jwtAuthenticationFaillHandler);
        return authenticationWebFilter;
    }

    @Bean
    public WebSessionServerSecurityContextRepository getSessionServerSecurityContextRepository(){
        return new WebSessionServerSecurityContextRepository();
    }

    /**
     * 鉴权配置，由于我们没有用默认的{@link WebSessionServerSecurityContextRepository}通过session来存储加载SecurityContext，
     * 所以需要再配置一个鉴权的Filter用于对每一个请求进行判断是否具备访问权限（检查header中的token的有效性）。当然如果使用session机制
     * 来存储的话，可以在authenticationWebFilter这个配置上将setSecurityContextRepository配成{@link WebSessionServerSecurityContextRepository}
     * 此处的鉴权配置就可以注释掉了。这里不需要去重写success和failure两个handler实现，其内部本身就有默认的handler实现，不会影响接口的返回内容。
     */
    @Bean
    public AuthenticationWebFilter authentzationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(customAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter);
        authenticationWebFilter.setRequiresAuthenticationMatcher(jwtAuthentizationServerWebExchangeMatcher);
        authenticationWebFilter.setSecurityContextRepository(jwtSecurityContextRepository);
//        authenticationWebFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
//        authenticationWebFilter.setAuthenticationFailureHandler(jwtAuthenticationFaillHandler);
        return authenticationWebFilter;
    }
/*
    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        ServerWebExchangeMatcher pathPatternParserServerWebExchangeMatcher = new PathPatternParserServerWebExchangeMatcher("/auth/login");
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(customAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter();
        authenticationWebFilter.setRequiresAuthenticationMatcher(pathPatternParserServerWebExchangeMatcher);
        authenticationWebFilter.setSecurityContextRepository(jwtSecurityContextRepository);
        authenticationWebFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        authenticationWebFilter.setAuthenticationFailureHandler(authenticationFaillHandler);
        return authenticationWebFilter;
    }
*/


    //    @Bean
//    ReactiveAuthenticationManager reactiveAuthenticationManager() {
//        final ReactiveUserDetailsService detailsService = userDetailsService();
//        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
//        managers.add(authentication -> {
//            // 其他登陆方式 (比如手机号验证码登陆) 可在此设置不得抛出异常或者 Mono.error
//            return Mono.empty();
//        });
//        // 必须放最后不然会优先使用用户名密码校验但是用户名密码不对时此 AuthenticationManager 会调用 Mono.error 造成后面的 AuthenticationManager 不生效
//        managers.add(new UserDetailsRepositoryReactiveAuthenticationManager(detailsService));
//        return new DelegatingReactiveAuthenticationManager(managers);
//    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); //默认
    }
}
