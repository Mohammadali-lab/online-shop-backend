package com.luv2code.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreationDTO {

    private Long id;

    @NotBlank
    @NotNull
    private String email;

    private String password;

    private String newPassword;

    @NotBlank
    @NotNull
    private String mobileNumber;

    @NotBlank
    @NotNull
    private String firstName;

    @NotBlank
    @NotNull
    private String lastName;

    @NotBlank
    @NotNull
    private String role;

    private Boolean isActive;

}
