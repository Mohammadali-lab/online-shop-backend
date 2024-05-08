package com.luv2code.ecommerce.dao;

import com.luv2code.ecommerce.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUsername(String phoneNumber);
}
