package com.bank.bankapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePhoneCodeRequest {

    @NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$")
    private String phoneNumber;

}
