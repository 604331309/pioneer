package com.slk.drools.db.context;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * KieSession助手类
 *
 * @author lshao
 * @date 2019/4/15 22:29
 */
@Component
public class DroolsSessionHelper {

    @Autowired
    private DroolsLoader droolsLoader;

    /**
     * 获取KieSession
     *
     * @param sceneId 场景ID
     * @return KieSession
     */
    public KieSession getKieSession(long sceneId,long ruleId) {
        return droolsLoader.getKieContainer(sceneId,ruleId).getKieBase().newKieSession();
    }

    public void reload(long sceneId){
        this.droolsLoader.reload(sceneId);
    }
}