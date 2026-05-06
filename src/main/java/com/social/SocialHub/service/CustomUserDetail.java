package com.social.SocialHub.service;

import com.social.SocialHub.entity.Roles;
import com.social.SocialHub.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetail implements UserDetails {

    // 🔥 2 modes
    private UserEntity user;
    private UUID id;
    private String username;
    private Roles role;

    // ✅ DB constructor
    public CustomUserDetail(UserEntity user) {
        this.user = user;
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
    }

    // ✅ JWT constructor (NO DB)
    public CustomUserDetail(UUID id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = Roles.valueOf(role);
    }

    // 🔥 Authorities
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    // 🔐 Password (only DB mode)
    @Override
    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }

    // 👤 Username
    @Override
    public String getUsername() {
        return username;
    }

    // ⏳ Account checks
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user == null || !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user == null || user.isActive();
    }

    // 🔥 Custom getters
    public UUID getId() {
        return id;
    }

    public Roles getRole() {
        return role;
    }

    public UserEntity getUser() {
        return this.user;
    }
}