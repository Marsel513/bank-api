package com.bank.bankapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordConfirmRequest {

    @NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$")
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 6)
    private String code;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")
    private String newPassword;

}
