package com.slk.drools.db.repository.model;

import com.slk.tester.jpa.model.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * drools规则持久化
 * <br/>08/01/2020 14:04
 *
 * @author lshao
 */
@Data
@Entity
@Table(name = "rule_info")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DBRuleInfo extends BasePo {

    //optional 属性实际上指定关联类与被关联类的join 查询关系，如optional=false 时join 查询关系为inner join, optional=true 时join 查询关系为left join。
    @ManyToOne(cascade={CascadeType.REFRESH},optional=true,fetch=FetchType.LAZY)
    @JoinColumn(name="scene_id",updatable=false,foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    private DBSceneInfo sceneInfo;//所属场景

    @Column(name = "rule_desc", length = 50)
    private String desc;

    /**
     * 规则内容，既drl文件内容
     */
    @Lob  // 大对象，映射 MySQL 的 Long Text 类型
    @Basic(fetch = FetchType.LAZY) // 懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(min = 2)
    @Column(nullable = false) // 映射为字段，值不能为空
    private String content;
}
