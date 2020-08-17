package com.slk.docker.controller;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/docker")
public class DockerController {

    @RequestMapping(value = "/request")
    String saveStaffInfo(@RequestBody String request){

        return request;
    }

}
