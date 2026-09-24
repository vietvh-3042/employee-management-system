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

    private static final int MIN_PASSWORD_LENGTH = 12;
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

        validatePassword(password);

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

    private void validatePassword(String candidate) {
        if (candidate.length() < MIN_PASSWORD_LENGTH
                || !candidate.matches(".*[A-Z].*")
                || !candidate.matches(".*[a-z].*")
                || !candidate.matches(".*\\d.*")
                || !candidate.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalStateException(
                    "ADMIN_PASSWORD must be at least 12 characters and contain upper, lower, digit, and special character");
        }
    }
}
