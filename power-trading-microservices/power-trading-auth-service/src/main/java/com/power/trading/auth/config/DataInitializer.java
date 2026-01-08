package com.power.trading.auth.config;

import com.power.trading.auth.entity.User;
import com.power.trading.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 * 系统启动时自动创建测试账号
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initTestUsers();
    }

    private void initTestUsers() {
        // 创建购电商测试账号
        if (!userRepository.existsByUsername("buyer1")) {
            User buyer1 = new User();
            buyer1.setUsername("buyer1");
            buyer1.setPassword(passwordEncoder.encode("123456"));
            buyer1.setUserType(User.UserType.BUYER);
            buyer1.setCompanyName("测试购电公司A");
            buyer1.setContactInfo("13800138000");
            buyer1.setStatus(User.UserStatus.ACTIVE);
            buyer1.setCreateTime(LocalDateTime.now());
            buyer1.setUpdateTime(LocalDateTime.now());
            userRepository.save(buyer1);
            System.out.println("已创建测试账号: buyer1 / 123456 (购电商)");
        }

        if (!userRepository.existsByUsername("buyer2")) {
            User buyer2 = new User();
            buyer2.setUsername("buyer2");
            buyer2.setPassword(passwordEncoder.encode("123456"));
            buyer2.setUserType(User.UserType.BUYER);
            buyer2.setCompanyName("测试购电公司B");
            buyer2.setContactInfo("13900139000");
            buyer2.setStatus(User.UserStatus.ACTIVE);
            buyer2.setCreateTime(LocalDateTime.now());
            buyer2.setUpdateTime(LocalDateTime.now());
            userRepository.save(buyer2);
            System.out.println("已创建测试账号: buyer2 / 123456 (购电商)");
        }

        // 创建发电商测试账号
        if (!userRepository.existsByUsername("generator1")) {
            User generator1 = new User();
            generator1.setUsername("generator1");
            generator1.setPassword(passwordEncoder.encode("123456"));
            generator1.setUserType(User.UserType.GENERATOR);
            generator1.setCompanyName("测试发电公司A");
            generator1.setContactInfo("13700137000");
            generator1.setStatus(User.UserStatus.ACTIVE);
            generator1.setCreateTime(LocalDateTime.now());
            generator1.setUpdateTime(LocalDateTime.now());
            userRepository.save(generator1);
            System.out.println("已创建测试账号: generator1 / 123456 (发电商)");
        }

        if (!userRepository.existsByUsername("generator2")) {
            User generator2 = new User();
            generator2.setUsername("generator2");
            generator2.setPassword(passwordEncoder.encode("123456"));
            generator2.setUserType(User.UserType.GENERATOR);
            generator2.setCompanyName("测试发电公司B");
            generator2.setContactInfo("13600136000");
            generator2.setStatus(User.UserStatus.ACTIVE);
            generator2.setCreateTime(LocalDateTime.now());
            generator2.setUpdateTime(LocalDateTime.now());
            userRepository.save(generator2);
            System.out.println("已创建测试账号: generator2 / 123456 (发电商)");
        }

        // 创建管理员测试账号
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setUserType(User.UserType.ADMIN);
            admin.setCompanyName("系统管理员");
            admin.setContactInfo("admin@power-trading.com");
            admin.setStatus(User.UserStatus.ACTIVE);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("已创建测试账号: admin / 123456 (管理员)");
        }
    }
}
