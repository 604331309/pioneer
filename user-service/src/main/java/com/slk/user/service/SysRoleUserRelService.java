package com.slk.user.service;

import com.slk.user.repository.model.SysRole;
import com.slk.user.repository.model.SysRoleUserRel;
import com.slk.user.repository.model.SysRoleUserRelRepository;
import com.slk.user.repository.model.SysUser;
import org.springframework.stereotype.Service;

/**
 * @author lshao
 * 2020/7/28
 */
@Service
public class SysRoleUserRelService {

    private final SysRoleUserRelRepository sysRoleUserRelRepository;

    public SysRoleUserRelService(SysRoleUserRelRepository sysRoleUserRelRepository) {
        this.sysRoleUserRelRepository = sysRoleUserRelRepository;
    }

    public void insert() {
        SysRoleUserRel sysRoleUserRel = new SysRoleUserRel();
        SysRole sysRole = new SysRole();
        sysRole.setId(222L);
        SysUser sysUser = new SysUser();
        sysUser.setId(333L);
        sysRoleUserRel.setRole(sysRole);
        sysRoleUserRel.setUser(sysUser);
        this.sysRoleUserRelRepository.save(sysRoleUserRel);
    }
}
