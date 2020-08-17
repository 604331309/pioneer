package com.slk.cgateway.security.converter;

import com.slk.cgateway.utils.jwt.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * {@link ServerAuthenticationConverter}用于将请求进行业务处理并生成{@link Authentication}
 * 此处是将请求头中的token拿出来做check，check通过则生成一个认证过的Authentication
 *
 * @author lshao
 */
@Slf4j
public class JWTAuthenticationConverter implements ServerAuthenticationConverter {
    private final JwtTokenUtils jwtTokenUtils;

    public JWTAuthenticationConverter(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public static final String Bearer = "bearer ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        Jwt jwt = null;
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.toLowerCase().startsWith(Bearer)) {
            return Mono.empty();
        }

        String token = authorization.length() <= Bearer.length() ?
                "" : authorization.substring(Bearer.length(), authorization.length());
        try {
            jwt = jwtTokenUtils.getJWT(token);
            log.debug(jwt.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }

        String username = "HC_admin";
        String password = "HC_admin";
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        usernamePasswordAuthenticationToken.setDetails(jwt);
        return Mono.just(usernamePasswordAuthenticationToken);
    }
}
