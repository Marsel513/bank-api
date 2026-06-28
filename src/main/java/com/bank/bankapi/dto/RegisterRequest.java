package com.bank.bankapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$")
    private String phoneNumber;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")
    private String password;

    @NotBlank
    @Size(min = 2, max = 255)
    private String fullName;


    @Pattern(regexp = "^\\d{4,6}$")
    private String pin;
}
