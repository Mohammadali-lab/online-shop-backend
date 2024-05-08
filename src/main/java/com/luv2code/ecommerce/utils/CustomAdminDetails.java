package com.luv2code.ecommerce.utils;

import com.luv2code.ecommerce.entity.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomAdminDetails implements UserDetails {

    private Admin admin;

    public CustomAdminDetails (Admin admin) {

        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        admin.getRole().getPrivilegeRoles().forEach(x -> authorities.add(
                new SimpleGrantedAuthority(x.getPrivilege().getPrivilege().toString()))
        );
        return authorities;
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return admin.isEnabled();
    }
}
