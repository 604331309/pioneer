package com.slk.oauth2.context;

import com.slk.oauth2.context.bean.GrantedAuthorityImpl;
import com.slk.oauth2.context.bean.UserImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 自定义的用户查询service
 */
public class DomainUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    public DomainUserDetailsService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetails> userDetailsList = new ArrayList<>();
        userDetailsList.add(new UserImpl("root", passwordEncoder.encode("1234"), Collections.singleton(new GrantedAuthorityImpl("root"))));
        userDetailsList.add(new UserImpl("admin", passwordEncoder.encode("1234"), Collections.singleton(new GrantedAuthorityImpl("admin"))));
        return userDetailsList.stream().filter(userDetails -> StringUtils.equals(userDetails.getUsername(), username)).findFirst().orElse(null);
    }
}
