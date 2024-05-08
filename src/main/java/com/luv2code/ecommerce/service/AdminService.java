package com.luv2code.ecommerce.service;

import com.luv2code.ecommerce.dao.AdminRepository;
import com.luv2code.ecommerce.entity.Admin;
import com.luv2code.ecommerce.exception.UserNotAllowedException;
import com.luv2code.ecommerce.utils.CustomAdminDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class AdminService {

    private AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (!admin.isPresent()) {
            return null;
        } else if (!admin.get().isEnabled()) {
            throw new UserNotAllowedException("You Are Not Allowed To Login! ");
        }
        return new CustomAdminDetails(admin.get());
    }
    
}
