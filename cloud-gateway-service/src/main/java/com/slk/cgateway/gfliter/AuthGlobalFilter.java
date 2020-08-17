package com.slk.cgateway.gfliter;

import com.google.gson.JsonObject;
import com.slk.cgateway.utils.jwt.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

//自定义全局过滤器需要实现GlobalFilter和Ordered接口
@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    // 当前过滤器的优先级，值越小，优先级越高
    @Override
    public int getOrder() {
        return 0;
    }


    @Autowired
    private JwtTokenUtils jwtTokenUtils;
/*    //完成判断逻辑
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        if (!StringUtils.equals(token, "admin")) {
            log.debug("鉴权失败");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        //调用chain.filter继续向下游执行
        return chain.filter(exchange);
    }*/
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, GatewayFilterChain webFilterChain) {
        ServerHttpRequest request=  serverWebExchange.getRequest();
        /*if(request.getPath().value().contains("login")){
            return webFilterChain.filter(serverWebExchange);
        }*/
        ServerHttpResponse response=serverWebExchange.getResponse();
        String authorization=request.getHeaders().getFirst("Authorization");
        if(authorization == null || ! authorization.startsWith("Bearer ")){
            return this.setErrorResponse(response,"未携带token");
        }
        String token=authorization.substring(7);
        try {
            System.out.println(jwtTokenUtils.getSpecifyField(token,"user_name"));
            serverWebExchange.getAttributes().put("user",token);
        }catch(Exception e) {
            return this.setErrorResponse(response,e.getMessage());
        }

        log.debug(">>>>>>>>>>>>>>>> this is a pre filter");
        return  webFilterChain.filter(serverWebExchange).then(Mono.fromRunnable(()->{
            log.info(">>>>>>>>>>>>>>>> this is a post filter");
        }));
    }

    protected Mono<Void> setErrorResponse(ServerHttpResponse response, String message){
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("status_code",500);
        jsonObject.addProperty("data",message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(jsonObject.toString().getBytes())));

    }
}
