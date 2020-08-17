package com.slk.cgateway.security.matcher;

import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult.match;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult.notMatch;

/**
 * jwt token的自定义匹配器，用于通知{@link WebFilter}对象中是否执行后续的逻辑，
 * 如果matches方法不通过则不执行当前的filter，但是会继续调用后续的filter chain
 *
 * @author lshao
 */
public class JWTAuthentizationServerWebExchangeMatcher implements ServerWebExchangeMatcher {


    ServerAuthenticationConverter bearerTokenConverter;


    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return this.bearerTokenConverter.convert(exchange)
                .flatMap(this::nullAuthentication)
                .onErrorResume(e -> notMatch());
    }


    public void setJWTBearerTokenServerWebExchangeMatcher(ServerAuthenticationConverter bearerTokenConverter) {
        Assert.notNull(bearerTokenConverter, "bearerTokenConverter cannot be null");
        this.bearerTokenConverter = bearerTokenConverter;
    }

    private Mono<MatchResult> nullAuthentication(Authentication authentication) {
        return authentication == null ? notMatch() : match();
    }
}
