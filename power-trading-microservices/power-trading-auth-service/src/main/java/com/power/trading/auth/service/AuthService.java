package com.power.trading.auth.service;

import com.power.trading.auth.dto.LoginRequest;
import com.power.trading.auth.dto.RegisterRequest;
import com.power.trading.auth.entity.User;
import com.power.trading.auth.repository.UserRepository;
import com.power.trading.common.core.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserType(User.UserType.valueOf(request.getUserType().toUpperCase()));
        user.setCompanyName(request.getCompanyName());
        user.setContactInfo(request.getContactInfo());
        user.setStatus(User.UserStatus.ACTIVE);

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType().name());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", convertToUserDTO(user));

        return result;
    }

    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("用户已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType().name());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", convertToUserDTO(user));

        return result;
    }

    private Map<String, Object> convertToUserDTO(User user) {
        Map<String, Object> userDTO = new HashMap<>();
        userDTO.put("id", user.getId());
        userDTO.put("username", user.getUsername());
        userDTO.put("userType", user.getUserType().name());
        userDTO.put("companyName", user.getCompanyName());
        userDTO.put("contactInfo", user.getContactInfo());
        userDTO.put("status", user.getStatus().name());
        return userDTO;
    }
}
