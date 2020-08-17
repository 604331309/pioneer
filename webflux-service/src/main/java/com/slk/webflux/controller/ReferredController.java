package com.slk.webflux.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class ReferredController {

    @RequestMapping("/getAsync")
    public DeferredResult<String> getAsync(){
        final Long start = System.currentTimeMillis();
        DeferredResult<String> result = new DeferredResult<>(50L);
        result.setResult("结果");
        result.onCompletion(()->{
            System.out.println("执行完成:"+(System.currentTimeMillis()-start));
        });
        result.onTimeout(()->{
            System.out.println("超时:"+(System.currentTimeMillis()-start));
        });
        return result;
    }
}
