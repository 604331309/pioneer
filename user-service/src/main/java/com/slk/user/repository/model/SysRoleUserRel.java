package com.slk.user.repository.model;

import com.slk.tester.jpa.model.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * <br/>08/01/2020 14:04
 *
 * @author lshao
 */

@Data
@Entity
@Table(name = "sys_role_user_rel",indexes = {@Index(columnList = "role_id"),@Index(columnList = "user_id")})
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SysRoleUserRel extends BasePo {

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JoinColumn(name="role_id",foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
    private SysRole role;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
    private SysUser user;
}
