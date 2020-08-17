package com.slk.drools.db.controller;

import com.slk.drools.db.entity.Order;
import com.slk.drools.db.repository.model.DBSceneInfo;
import com.slk.drools.db.service.DroolsService;
import com.slk.drools.demo.service.KieSessionHelper;
import com.slk.drools.demo.service.RuleLoader;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 规则测试
 *
 * @author lshao
 */
@RequestMapping("/drools")
@RestController
public class DroolsController {

    private DroolsService droolsService;

    public DroolsController(DroolsService droolsService) {
        this.droolsService = droolsService;
    }

    @GetMapping("/")
    public List<DBSceneInfo> findAll() {
        return this.droolsService.getAllSceneInfo();
    }

    @GetMapping("/exec/{sceneId}/{ruleId}")
    public String exec(@PathVariable("sceneId") Long sceneId, @PathVariable("ruleId") Long ruleId) {
        //输入数据到规则引擎(drools叫Fact对象)
        Order order = new Order(99d);
        droolsService.exec(sceneId, ruleId, order);
        System.out.println("sceneId:" + sceneId + "  " + "ruleId:" + ruleId);
        return "sceneId:" + sceneId + "  " + "ruleId:" + ruleId;
    }

    @GetMapping("/reload/{sceneId}")
    public void reload(@PathVariable("sceneId") Long sceneId) {
        this.droolsService.reload(sceneId);
    }

}