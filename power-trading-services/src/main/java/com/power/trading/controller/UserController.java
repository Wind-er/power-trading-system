package com.power.trading.controller;

import com.power.trading.dto.ApiResponse;
import com.power.trading.entity.User;
import com.power.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ApiResponse<?> getProfile(@RequestParam(required = false) Long userId,
                                     @RequestParam(required = false) String username) {
        User user;
        if (userId != null) {
            user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
        } else if (username != null) {
            user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
        } else {
            return ApiResponse.error("请提供userId或username");
        }
        return ApiResponse.success(user);
    }

    @PutMapping("/profile")
    public ApiResponse<?> updateProfile(@RequestParam Long userId, @RequestBody User userDetails) {
        try {
            return ApiResponse.success(userService.updateUserProfile(userId, userDetails));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ApiResponse<?> updateUserStatus(@PathVariable Long id, @RequestBody User.UserStatus status) {
        try {
            return ApiResponse.success(userService.updateUserStatus(id, status));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

