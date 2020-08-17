package com.slk.user.repository.model;

import com.slk.tester.jpa.model.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 角色
 *
 * @author lshao
 * 2020/7/20
 */
@Data
@Entity
@Table(name = "sys_role",indexes = {@Index(columnList = "role_code")})
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SysRole extends BasePo {
    @Column(name = "role_name", length = 32)
    private String roleName;
    @Column(name = "role_code", length = 32)
    private String roleCode;
}
