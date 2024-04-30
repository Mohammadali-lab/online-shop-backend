package com.luv2code.ecommerce.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class ApiError {

    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiError(HttpStatus status, String message, String cause) {
        this.status = status;
        this.message = message;
        this.errors = Collections.singletonList(cause);
    }

    public ApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

