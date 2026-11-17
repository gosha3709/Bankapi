package com.yehor.bankapi.controller;

import com.yehor.bankapi.dto.request.LoginRequest;
import com.yehor.bankapi.dto.request.RegisterRequest;
import com.yehor.bankapi.service.BankService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final BankService bankService;

    public AuthController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/login")
    public boolean login(@Valid @RequestBody LoginRequest request) {
        return  bankService.login(request.email(), request.password());
    }

    @PostMapping("/register")
    public void createAccount(@Valid @RequestBody RegisterRequest request) {
        bankService.registerAccount(request.name(), request.lastName(), request.email(), request.password());
    }
}
