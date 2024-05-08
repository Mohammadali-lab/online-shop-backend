package com.luv2code.ecommerce.exception;


import org.springframework.security.core.AuthenticationException;


public class UserNotAllowedException extends AuthenticationException {
    public UserNotAllowedException(String explanation) {
        super(explanation);
    }
}
