package com.slk.drools.db.service;

import com.slk.drools.db.context.DroolsSessionHelper;
import com.slk.drools.db.repository.SceneInfoRepository;
import com.slk.drools.db.repository.model.DBSceneInfo;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class DroolsService {

    private final SceneInfoRepository sceneInfoRepository;

    private final DroolsSessionHelper droolsSessionHelper;

    public DroolsService(SceneInfoRepository sceneInfoRepository, DroolsSessionHelper droolsSessionHelper) {
        this.sceneInfoRepository = sceneInfoRepository;
        this.droolsSessionHelper = droolsSessionHelper;
    }

    /**
     * 获取所有场景的信息
     *
     * @return
     */
    public List<DBSceneInfo> getAllSceneInfo() {
        return sceneInfoRepository.findAll();
    }

    public DBSceneInfo getSceneInfoById(Long sceneId) {
        return sceneInfoRepository.getOne(sceneId);
    }

    public void exec(long senceId, long ruleId, Object... object) {
        KieSession kieSession = this.droolsSessionHelper.getKieSession(senceId, ruleId);
        Arrays.asList(object).forEach(kieSession::insert);
        kieSession.fireAllRules();
        kieSession.dispose();
    }

    public void reload(long senceId){
        this.droolsSessionHelper.reload(senceId);
    }

}
