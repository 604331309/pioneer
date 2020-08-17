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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult.match;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult.notMatch;

/**
 * jwt token的自定义匹配器，用于通知{@link WebFilter}对象中是否执行后续的逻辑，
 * 如果matches方法不通过则不执行当前的filter，但是会继续调用后续的filter chain
 *
 * @author lshao
 */
public class JWTAuthenticationServerWebExchangeMatcher implements ServerWebExchangeMatcher {
    private static final PathPatternParser DEFAULT_PATTERN_PARSER = new PathPatternParser();
    /**
     * 默认的授权请求地址
     */
    private final String TARGET_PATTERN = "/auth/login";


    private final PathPattern pattern = DEFAULT_PATTERN_PARSER.parse(TARGET_PATTERN);
    ;


    ServerAuthenticationConverter bearerTokenConverter;


    /**
     * header头中必须有beaer jwt-token并且只能是/oauth/login的请求，如果此处不限定请求路径则会将所有的请求都会进行处理
     *
     * @param exchange
     * @return
     */
    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        if (this.matchesPattern(exchange)) {
            return this.bearerTokenConverter.convert(exchange)
                    .flatMap(this::nullAuthentication)
                    .onErrorResume(e -> notMatch());
        } else {
            return MatchResult.notMatch();
        }
    }

    /**
     * 参考 {@link PathPatternParserServerWebExchangeMatcher}
     *
     * @param exchange
     * @return
     */
    public boolean matchesPattern(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        PathContainer path = request.getPath().pathWithinApplication();
        return this.pattern.matches(path);
    }

    public void setJWTBearerTokenServerWebExchangeMatcher(ServerAuthenticationConverter bearerTokenConverter) {
        Assert.notNull(bearerTokenConverter, "bearerTokenConverter cannot be null");
        this.bearerTokenConverter = bearerTokenConverter;
    }

    private Mono<MatchResult> nullAuthentication(Authentication authentication) {
        return authentication == null ? notMatch() : match();
    }
}
