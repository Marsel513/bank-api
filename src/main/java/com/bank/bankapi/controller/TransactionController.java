package com.bank.bankapi.controller;

import com.bank.bankapi.dto.TransactionalResponse;
import com.bank.bankapi.dto.TransferRequest;
import com.bank.bankapi.entity.User;
import com.bank.bankapi.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionalResponse> transfer(@RequestBody @Valid TransferRequest request) {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.status(201).body(transactionService.transfer(user, request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionalResponse>> getTransactions(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return ResponseEntity.ok(transactionService.getTransactions(user));
    }

}
