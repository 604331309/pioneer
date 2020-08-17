package com.slk.drools.db.repository;

import com.slk.drools.db.repository.model.DBRuleInfo;
import com.slk.drools.db.repository.model.DBSceneInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <br/>06/01/2020 14:22
 *
 * @author lshao
 */
public interface SceneInfoRepository extends JpaRepository<DBSceneInfo, Long>, JpaSpecificationExecutor<DBSceneInfo> {

}
