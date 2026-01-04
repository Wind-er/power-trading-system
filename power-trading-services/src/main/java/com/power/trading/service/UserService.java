package com.power.trading.service;

import com.power.trading.entity.User;
import com.power.trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User updateUserProfile(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (userDetails.getCompanyName() != null) {
            user.setCompanyName(userDetails.getCompanyName());
        }
        if (userDetails.getContactInfo() != null) {
            user.setContactInfo(userDetails.getContactInfo());
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUserStatus(Long userId, User.UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setStatus(status);
        return userRepository.save(user);
    }
}

