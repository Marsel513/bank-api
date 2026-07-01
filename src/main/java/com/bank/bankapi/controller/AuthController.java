package com.bank.bankapi.controller;

import com.bank.bankapi.dto.*;
import com.bank.bankapi.service.PasswordResetService;
import com.bank.bankapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordResetService resetService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request){
        userService.register(request);
        return ResponseEntity.status(201).body("User successfully registered");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request){
        LoginResponse response = userService.login(request);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<String> resetPasswordPhoneCode(@RequestBody @Valid CreatePhoneCodeRequest request){
        resetService.CreatePhoneCode(request);
        return ResponseEntity.status(202).body("Code will be delivered to your phone number soon");
    }

    @PatchMapping("/password-reset/confirm")
    public ResponseEntity<String> resetPasswordConfirm(@RequestBody @Valid ResetPasswordConfirmRequest request){
        resetService.confirmReset(request);
        return ResponseEntity.status(200).body("Password is successfully changed");
    }


}
