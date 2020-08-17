package com.slk.user.controller;


import com.slk.user.service.SysRoleUserRelService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-info")
public class UserInfoController {

    private SysRoleUserRelService sysRoleUserRelService;

    public UserInfoController(SysRoleUserRelService sysRoleUserRelService) {
        this.sysRoleUserRelService = sysRoleUserRelService;
    }

    @RequestMapping("/")
    public String getUserInfo() {
        return "user-info";
    }

    @RequestMapping("test1")
    public Object test() {
        return "ok";
    }


    @RequestMapping("test2")
    public Object test2() {
        this.sysRoleUserRelService.insert();
        return "ok";
    }
}
