package com.power.trading.controller;

import com.power.trading.dto.ApiResponse;
import com.power.trading.dto.LoginRequest;
import com.power.trading.dto.RegisterRequest;
import com.power.trading.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ApiResponse<?> register(@Validated @RequestBody RegisterRequest request) {
        try {
            return ApiResponse.success(authService.register(request));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Validated @RequestBody LoginRequest request) {
        try {
            return ApiResponse.success(authService.login(request));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

