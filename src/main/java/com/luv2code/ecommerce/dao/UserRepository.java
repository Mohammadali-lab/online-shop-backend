package com.luv2code.ecommerce.dao;

import com.luv2code.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
