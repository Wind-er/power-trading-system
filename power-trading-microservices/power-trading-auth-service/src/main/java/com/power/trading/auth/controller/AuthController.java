package com.power.trading.auth.controller;

import com.power.trading.auth.dto.LoginRequest;
import com.power.trading.auth.dto.RegisterRequest;
import com.power.trading.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Map<String, Object> register(@Validated @RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("data", authService.register(request));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Validated @RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", authService.login(request));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
}
