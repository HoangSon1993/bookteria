package org.sondev.identity.configuration;

import java.util.HashSet;

import org.sondev.identity.entity.Role;
import org.sondev.identity.entity.User;
import org.sondev.identity.repository.RoleRepository;
import org.sondev.identity.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    String ADMIN_USER_NAME = "admin";
    String ADMIN_PASSWORD = "admin";

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
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("init appilication ......");
        return args -> {
            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                var adminRole = roleRepository.save(
                        Role.builder().name("ADMIN").description("Admin role").build());
                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User user = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it.");
            }
        };
    }
}
