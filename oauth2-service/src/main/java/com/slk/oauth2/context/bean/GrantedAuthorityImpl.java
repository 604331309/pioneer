package com.slk.oauth2.context.bean;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

/**
 * 权限实体
 */
@Setter
@AllArgsConstructor
public class GrantedAuthorityImpl implements GrantedAuthority {

    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
