package com.slk.webflux.controller;


import com.slk.webflux.entity.CommonRequest;
import com.slk.webflux.entity.Home;
import com.slk.webflux.utils.ThreadUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@RestController
public class ReactorController {
    @GetMapping("/hello")
    public Mono<String> hello() {   // 【改】返回类型为Mono<String>
        return Mono.just("Welcome to reactive world ~");     // 【改】使用Mono.just生成响应式数据
    }

    @GetMapping(value = "/hello-stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> hello2() {
        return Flux.range(1, 6)
                .subscribeOn(Schedulers.elastic())//可以直接从range开始启动子线程进行处理
                .map(i -> 10 / (i - 0))
                .map(i -> i * i)
                .delayElements(Duration.ofMillis(1000))
                .onErrorReturn(0);
    }

    @GetMapping(value = "/hello-home")
    public Flux<Integer> hello2(@RequestBody Mono<Home> homeMono) {
        return homeMono.flatMap(home -> {
            System.out.println(home.toString());
            return Mono.just(home.getHomeAge());
        }).flux();
    }

    @GetMapping(value = "/hello-request")
    public Mono<Home> hello3(ServerWebExchange exchange, @RequestBody Mono<Home> homeMono) {

        System.out.println(exchange.getRequest().getPath());
        return homeMono
                .publishOn(Schedulers.elastic())
                .map(home -> {
                    System.out.println(home.getName());
                    return home;
                });
    }

    @GetMapping(value = "/hello-common")
    public Mono<Home> hello4(ServerWebExchange exchange, @RequestBody Mono<CommonRequest<Home>> homeMono) {

        System.out.println(exchange.getRequest().getPath());
        return homeMono
                .flatMap(home -> {
                    System.out.println(home.toString());
                    return Mono.just(home.getData());
                })
                .onErrorReturn(new Home());
    }
}
