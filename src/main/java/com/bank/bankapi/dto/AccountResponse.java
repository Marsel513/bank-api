package com.bank.bankapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AccountResponse {

    private long id;

    private String currency;

    private BigDecimal balance;

    private LocalDateTime createdAt;

}
