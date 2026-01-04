package com.power.trading.dto;

import lombok.Data;
import com.power.trading.entity.User;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "用户类型不能为空")
    private String userType;

    private String companyName;
    private String contactInfo;
}

