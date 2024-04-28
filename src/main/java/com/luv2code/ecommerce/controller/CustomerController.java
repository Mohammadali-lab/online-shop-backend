package com.luv2code.ecommerce.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.luv2code.ecommerce.config.CustomerAuthenticationManager;
import com.luv2code.ecommerce.dto.UserAuthRequestDTO;
import com.luv2code.ecommerce.dto.UserAuthResponseDTO;
import com.luv2code.ecommerce.dto.UserResponseDTO;
import com.luv2code.ecommerce.service.UserService;
import com.luv2code.ecommerce.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.badRequest;

@RestController
public class CustomerController {

    private UserService userService;
    private static JwtUtil jwtUtil;

    private CustomerAuthenticationManager authenticationManager;

    public CustomerController(UserService userService,
                              JwtUtil jwtUtil,
                              CustomerAuthenticationManager authenticationManager){
        this.userService = userService;
        CustomerController.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserAuthRequestDTO userAuthRequestDTO,
                                           HttpServletRequest request) throws JsonProcessingException {
        UserDetails userDetails = userService.loadUserByUsername(userAuthRequestDTO.getPhoneNumber());
        if (userDetails != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        } else {
            UserResponseDTO response = new UserResponseDTO();
            response = userService.createUser(userAuthRequestDTO);
            userDetails = userService.loadUserByUsername(userAuthRequestDTO.getPhoneNumber());
            String jwt = jwtUtil.generateToken(userDetails, false);
            return ResponseEntity.ok(new UserAuthResponseDTO(jwt, "token returned successfully."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponseDTO> login(@RequestBody UserAuthRequestDTO userDTO,
                                        HttpServletRequest request) throws InterruptedException {


        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getPhoneNumber(), userDTO.getPassword()));

            UserDetails userDetails = userService.loadUserByUsername(userDTO.getPhoneNumber());
            String jwt = jwtUtil.generateToken(userDetails, false);
            return ResponseEntity.ok(new UserAuthResponseDTO(jwt, "token returned successfully."));

        } catch (BadCredentialsException e) {
//            logger.error("Incorrect username and password");
            return ResponseEntity.notFound().build();
        }

    }

}
