package com.luv2code.ecommerce.dto;

import lombok.Data;

@Data
public class UserAuthRequestDTO {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
}
