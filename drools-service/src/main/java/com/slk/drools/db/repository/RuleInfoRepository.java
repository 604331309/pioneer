package com.slk.drools.db.repository;

import com.slk.drools.db.repository.model.DBRuleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <br/>06/01/2020 14:22
 *
 * @author lshao
 */
public interface RuleInfoRepository extends JpaRepository<DBRuleInfo, Long>, JpaSpecificationExecutor<DBRuleInfo> {

}
