package com.luv2code.ecommerce.utils;

import com.luv2code.ecommerce.entity.BaseEntity;
import com.luv2code.ecommerce.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created By Alireza Dolatabadi
 * Date: 3/27/2021
 * Time: 8:45 AM
 */
public class CustomUserDetails implements UserDetails {

    private final BaseEntity user;

    public CustomUserDetails(BaseEntity user) {
        this.user = user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        if (user instanceof User) {
            return ((User) user).getPassword();
        }
        return null;
    }

    @Override
    public String getUsername() {
        if (user instanceof User) {
            return ((User) user).getUsername();
        }
        return null;
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
        if (user instanceof User) {
            return ((User) user).isEnabled();
        }
        return false;
    }
}
