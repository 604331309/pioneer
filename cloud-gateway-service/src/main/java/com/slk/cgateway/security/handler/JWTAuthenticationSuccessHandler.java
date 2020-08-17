package com.slk.cgateway.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.slk.cgateway.security.entity.AuthUserDetails;
import com.slk.cgateway.security.response.MessageCode;
import com.slk.cgateway.security.response.WsResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Component
public class JWTAuthenticationSuccessHandler extends WebFilterChainServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        //设置body
        WsResponse wsResponse = WsResponse.success();
        byte[] dataBytes = {};
        ObjectMapper mapper = new ObjectMapper();
        try {
            Jwt jwt = (Jwt) authentication.getDetails();
            List<GrantedAuthority> list = new ArrayList<>();
            list.add(new SimpleGrantedAuthority("role"));
            AuthUserDetails userDetails = buildUser(new User("111", "222", list));
            byte[] authorization = (userDetails.getUsername() + ":" + userDetails.getPassword()).getBytes();
            String token = Base64.getEncoder().encodeToString(authorization);
            httpHeaders.add(HttpHeaders.AUTHORIZATION, jwt.getEncoded());
            wsResponse.setResult(jwt);
            dataBytes = mapper.writeValueAsBytes(wsResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
            JsonObject result = new JsonObject();
            result.addProperty("status", MessageCode.COMMON_FAILURE.getCode());
            result.addProperty("message", "授权异常");
            dataBytes = result.toString().getBytes();
        }
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }


    private AuthUserDetails buildUser(User user) {
        AuthUserDetails userDetails = new AuthUserDetails();
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword().substring(user.getPassword().lastIndexOf("}") + 1, user.getPassword().length()));
        return userDetails;
    }


}
