package com.slk.drools.db.repository.model;

import com.slk.tester.jpa.model.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/**
 * 场景信息
 * <br/>08/01/2020 14:04
 *
 * @author lshao
 */
@Data
@Entity
@Table(name = "scene_info")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DBSceneInfo extends BasePo {

    /**
     * 场景code唯一，一个场景对应多个drl定义
     */
    @Column(name = "scene_code", length = 50)
    private String sceneCode;

    @Column(name = "scene_desc", length = 50)
    private String desc;



    //拥有mappedBy注解的实体类为关系被维护端
    @OneToMany(mappedBy = "sceneInfo",cascade= {CascadeType.ALL},fetch=FetchType.EAGER)
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<DBRuleInfo> ruleInfoList;//drl规则列表
}
