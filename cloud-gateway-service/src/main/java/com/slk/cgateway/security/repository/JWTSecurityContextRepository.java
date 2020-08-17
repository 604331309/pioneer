package com.slk.cgateway.security.repository;

import com.slk.cgateway.security.manager.CustomAuthenticationManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 用于实现存储与获取securityContext
 * 此处是用于jwt的逻辑，不需要用的session则save可以不作任何事情，load方法直接从请求中的jwt-token做解析
 */
@Component
public class JWTSecurityContextRepository implements ServerSecurityContextRepository {

    private final CustomAuthenticationManager authenticationManager;

    public JWTSecurityContextRepository(CustomAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // todo 需要用JwtTokenUtils做解析
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            Authentication auth = null;
            auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
            return  this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
        } else {
            return Mono.empty();
        }
    }
}
