package com.sondev.identity_service.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sondev.identity_service.entity.User;
import com.sondev.identity_service.enums.Role;
import com.sondev.identity_service.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    public ApplicationInitConfig(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @General: Tạo ra User Admin khi chương trình start.
     * @Description: Kiểm tra có tài khoản admin chưa, nếu chưa có mới tạo tài khoản admin.
     * @Note: {@code @ConditionalOnProperty} : Khởi chay bean có điều kiện.
     * Điều kiện ở đây là spring.datasource.driver-class-name : com.mysql.cj.jdbc.Driver
     **/
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver",
            matchIfMissing = false)
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("init appilication ......");
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        // .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it.");
            }
        };
    }
}
