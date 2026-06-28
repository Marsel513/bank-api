package com.bank.bankapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionalResponse {

    private Long id;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime createdAt;

    private Long fromAccountId;

    private Long toAccountId;
}
