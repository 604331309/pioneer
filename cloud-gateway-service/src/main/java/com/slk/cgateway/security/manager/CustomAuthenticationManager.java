package com.slk.cgateway.security.manager;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToekn=authentication.getCredentials().toString();
        try {
            //todo 此处应该列出token中携带的角色表。
            List<String> roles=new ArrayList();
            roles.add("user");
            UsernamePasswordAuthenticationToken authentication1=new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
//            authentication.setAuthenticated(true);
            authentication1.setDetails(authentication.getDetails());
            return Mono.just(authentication1);
        } catch (Exception e) {
            throw  new BadCredentialsException(e.getMessage());
        }
    }
}
