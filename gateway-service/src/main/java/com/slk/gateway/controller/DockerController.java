package com.slk.gateway.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/docker")
public class DockerController {
    @Autowired
    private DockerFeign dockerFeign;

    @PostMapping(value = "/request")
    String saveStaffInfo(@RequestBody String request){

        return dockerFeign.saveStaffInfo(request);
    }
}
