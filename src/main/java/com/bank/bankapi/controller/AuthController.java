package com.bank.bankapi.controller;

import com.bank.bankapi.dto.LoginRequest;
import com.bank.bankapi.dto.LoginResponse;
import com.bank.bankapi.dto.RegisterRequest;
import com.bank.bankapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request){
        userService.register(request);
        return ResponseEntity.status(201).body("User succesfully registrated");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request){
        LoginResponse response = userService.login(request);
        return ResponseEntity.status(200).body(response);
    }
}
