package com.bank.bankapi.controller;

import com.bank.bankapi.dto.AccountResponse;
import com.bank.bankapi.dto.CreateAccountRequest;
import com.bank.bankapi.entity.User;
import com.bank.bankapi.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("api/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;



    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody @Valid CreateAccountRequest request){
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        accountService.createAccount(user, request.getCurrency());
        return ResponseEntity.status(201).body("Account is created");
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllUserAccounts(){

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(accountService.getAccounts(user));
    }
}
