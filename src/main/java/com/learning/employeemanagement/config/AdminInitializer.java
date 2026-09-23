package com.learning.employeemanagement.config;

import com.learning.employeemanagement.entity.Role;
import com.learning.employeemanagement.entity.User;
import com.learning.employeemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.admin.username:}") String username,
            @Value("${app.security.admin.password:}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (username.isBlank() || password.isBlank()) {
            log.warn("Admin initializer skipped: admin credentials are not configured");
            return;
        }

        String normalizedUsername = username.trim();
        if (userRepository.existsByUsername(normalizedUsername)) {
            log.info("Admin user already exists: username={}", normalizedUsername);
            return;
        }

        userRepository.save(new User(
                normalizedUsername,
                passwordEncoder.encode(password),
                Role.ADMIN));
        log.info("Admin user created: username={}", normalizedUsername);
    }
}
