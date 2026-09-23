package com.learning.employeemanagement.service;

import com.learning.employeemanagement.dto.AuthResponse;
import com.learning.employeemanagement.dto.LoginRequest;
import com.learning.employeemanagement.dto.RegisterRequest;
import com.learning.employeemanagement.entity.Role;
import com.learning.employeemanagement.entity.User;
import com.learning.employeemanagement.repository.UserRepository;
import com.learning.employeemanagement.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }
        User user;
        try {
            user = userRepository.save(new User(
                    username, passwordEncoder.encode(request.password()), Role.USER));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered", exception);
        }
        return responseFor(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> invalidCredentials());
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw invalidCredentials();
        }
        return responseFor(user);
    }

    private AuthResponse responseFor(User user) {
        return new AuthResponse(
                jwtService.generateToken(user.getUsername(), user.getRole().name()),
                user.getUsername(),
                user.getRole().name());
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
}
