package com.slk.drools.db.context;

import com.slk.drools.db.repository.model.DBRuleInfo;
import com.slk.drools.db.repository.model.DBSceneInfo;
import com.slk.drools.db.service.DroolsService;
import com.slk.drools.demo.entity.RuleInfo;
import com.slk.drools.demo.service.RuleInfoService;
import org.apache.commons.lang.StringUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Component
public class DroolsLoader implements ApplicationRunner {
    /**
     * key1:场景id，value1:该场景下所有drl为粒度的KieContainer，key1-1：drl定义对应的id，vslue1-1：当前drl对应的kieContainer，每个drl对应一个KieContainer
     */
//    private final ConcurrentMap<String, KieContainer> kieContainerMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<Map<String, KieContainer>>> kieContainerMap = new ConcurrentHashMap<>();

    @Autowired
    private DroolsService droolsService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        reloadAll();
    }

    /**
     * 构造kcontainerName
     *
     * @param sceneId 场景ID
     * @return kcontainerName
     */
    private String buildSenceKcontainerName(long sceneId) {
        return "scene_" + sceneId;
    }
    private String buildRuleKcontainerName(long ruleId) {
        return "rule_" + ruleId;
    }

    /**
     * 构造kbaseName
     *
     * @param sceneId 场景ID
     * @return kbaseName
     */
    private String buildKbaseName(long sceneId, long ruleId) {
        return "kbase_" + sceneId + "_" + ruleId;
    }

    /**
     * 构造ksessionName
     *
     * @param sceneId 场景ID
     * @return ksessionName
     */
    private String buildKsessionName(long sceneId, long ruleId) {
        return "ksession_" + sceneId + "_" + ruleId;
    }

    List<Map<String, KieContainer>> getKieContainer(long sceneId) {
        return kieContainerMap.get(buildSenceKcontainerName(sceneId));
    }

    public KieContainer getKieContainer(long sceneId, long ruleId) {
        List<Map<String, KieContainer>> list = this.getKieContainer(sceneId);
        Optional<Map<String, KieContainer>> opt = list.stream().filter(stringKieContainerMap -> stringKieContainerMap.get(buildRuleKcontainerName(ruleId)) != null).findFirst();
        return opt.orElse(new HashMap<>()).get(buildRuleKcontainerName(ruleId));
    }

    /**
     * 重新加载所有规则
     */
    public void reloadAll() {
        List<DBSceneInfo> allSceneInfo = this.droolsService.getAllSceneInfo();
        allSceneInfo.forEach(dbSceneInfo -> {
            long sceneId = dbSceneInfo.getId();
            List<DBRuleInfo> ruleInfos = dbSceneInfo.getRuleInfoList();
            this.reload(sceneId, ruleInfos);
        });
        System.out.println("reload all success");

    }

    /**
     * 重新加载给定场景下的规则
     *
     * @param sceneId 场景ID
     */
    public void reload(Long sceneId) {
        DBSceneInfo sceneInfo = this.droolsService.getSceneInfoById(sceneId);
        this.reload(sceneId, sceneInfo.getRuleInfoList());
        System.out.println("reload success");
    }

    /**
     * 重新加载给定场景给定规则列表，对应一个kmodule
     *
     * @param sceneId   场景ID
     * @param ruleInfos 规则列表
     */
    private void reload(long sceneId, List<DBRuleInfo> ruleInfos) {
        KieServices kieServices = KieServices.get();


        List<Map<String, KieContainer>> kieContainerList = new ArrayList<>();
        for (DBRuleInfo ruleInfo : ruleInfos) {
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
            KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(buildKbaseName(sceneId, ruleInfo.getId()));
            kieBaseModel.setDefault(true);
            kieBaseModel.addPackage(MessageFormat.format("rules.scene_{0}.rule_{1}", String.valueOf(sceneId),String.valueOf(ruleInfo.getId())));
            kieBaseModel.newKieSessionModel(buildKsessionName(sceneId, ruleInfo.getId()));
            String fullPath = MessageFormat.format("src/main/resources/rules/scene_{0}/rule_{1}.drl", String.valueOf(sceneId), String.valueOf(ruleInfo.getId()));
            kieFileSystem.write(fullPath, ruleInfo.getContent());
            kieFileSystem.writeKModuleXML(kieModuleModel.toXML());
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                System.out.println(results.getMessages());
                throw new IllegalStateException("rule error");
            }

            KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
            Map<String, KieContainer> stringKieContainerHashMap = new HashMap<>();
            stringKieContainerHashMap.put(buildRuleKcontainerName(ruleInfo.getId()), kieContainer);
            kieContainerList.add(stringKieContainerHashMap);
        }
        kieContainerMap.put(buildSenceKcontainerName(sceneId), kieContainerList);


    }
}
