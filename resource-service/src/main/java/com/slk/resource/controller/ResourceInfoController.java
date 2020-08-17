package com.slk.resource.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class ResourceInfoController {

    @RequestMapping("/")
    public String getResourceInfo() {
        return "resource-info";
    }

    @RequestMapping("test1")
    public Object test() {
        return "ok";
    }
}
