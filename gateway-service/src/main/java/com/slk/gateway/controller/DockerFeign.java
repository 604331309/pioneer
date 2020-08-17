package com.slk.gateway.controller;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "docker-microservice", path = "/api/v1/docker")
public interface DockerFeign {

    @PostMapping(value = "/request")
    String saveStaffInfo(@RequestBody String request);

}
