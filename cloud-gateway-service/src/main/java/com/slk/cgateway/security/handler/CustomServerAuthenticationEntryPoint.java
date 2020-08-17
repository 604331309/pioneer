package com.slk.cgateway.security.handler;

import com.google.gson.JsonObject;
import com.slk.cgateway.security.response.MessageCode;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 此处自定义鉴权失败时的处理逻辑，{@link AuthenticationWebFilter#filter}方法中
 * matcher或者convert任其一个失败，则都会进入该类的commence方法
 */
@Component
public class CustomServerAuthenticationEntryPoint extends HttpBasicServerAuthenticationEntryPoint
        implements ServerAuthenticationEntryPoint {

    public CustomServerAuthenticationEntryPoint() {
    }


    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
//            response.getHeaders().set(HttpHeaders.AUTHORIZATION, this.headerValue);
        JsonObject result = new JsonObject();
        result.addProperty("status", MessageCode.COMMON_AUTHORIZED_FAILURE.getCode());
        result.addProperty("message", MessageCode.COMMON_AUTHORIZED_FAILURE.getMsg());
        byte[] dataBytes = result.toString().getBytes();
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }
}
