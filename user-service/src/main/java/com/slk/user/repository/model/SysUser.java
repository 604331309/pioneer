package com.slk.user.repository.model;

import com.slk.tester.jpa.model.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户
 *
 * @author lshao
 * 2020/7/18
 */
@Data
@Entity
@Table(name = "sys_user")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SysUser extends BasePo {
    @Column(name = "user_name", length = 50)
    private String userName;
    @Column(name = "age")
    private Integer age;
    @Column(name = "sex", length = 1)
    private String sex;
}
