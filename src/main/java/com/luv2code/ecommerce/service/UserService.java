package com.luv2code.ecommerce.service;

import com.luv2code.ecommerce.dao.UserRepository;
import com.luv2code.ecommerce.dto.UserAuthRequestDTO;
import com.luv2code.ecommerce.dto.UserResponseDTO;
import com.luv2code.ecommerce.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserDetailsService userDetailsService
            , PasswordEncoder passwordEncoder
            , UserRepository userRepository){
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    public UserResponseDTO createUser(UserAuthRequestDTO requestDTO){

        User user = new User();
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setUsername(requestDTO.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setEnabled(true);

        user = userRepository.save(user);
        UserResponseDTO res = new UserResponseDTO();
        res.setFirstName(user.getFirstName());
        res.setLastName(user.getLastName());
        res.setPhoneNumber(user.getUsername());
        return res;
    }

    public void disableUser(String username) {
        User user = userRepository.findByUsername(username);
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void enableUser(String username) {
        User user = userRepository.findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
    }
}


