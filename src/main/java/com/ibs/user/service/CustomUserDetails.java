package com.ibs.user.service;

import com.ibs.user.domain.Role;
import com.ibs.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

    private User user;
    private UUID id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    public CustomUserDetails(
            UUID id,
            String username,
            String name,
            String email,
            String profileImage,
            String jobTitle,
            Role role,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.username = username;
        this.password = "";
        this.authorities = authorities;

        this.user = User.builder()
                .id(id)
                .username(username)
                .name(name)
                .email(email)
                .profileImage(profileImage)
                .jobTitle(jobTitle)
                .role(role)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
