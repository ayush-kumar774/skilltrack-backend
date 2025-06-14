package org.havoc.skilltrack.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.havoc.skilltrack.auth.dto.*;
import org.havoc.skilltrack.auth.util.JwtUtil;
import org.havoc.skilltrack.exception.CustomException.EmailAlreadyExistsException;
import org.havoc.skilltrack.exception.CustomException.InvalidCredentialsException;
import org.havoc.skilltrack.exception.CustomException.EmailDoesNotExistsException;
import org.havoc.skilltrack.exception.CustomException.UsernameAlreadyExistsException;
import org.havoc.skilltrack.user.entity.Role;
import org.havoc.skilltrack.user.entity.User;
import org.havoc.skilltrack.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} is already registered", request.getEmail());
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username {} is already taken", request.getUsername());
            throw new UsernameAlreadyExistsException("Username is already taken");
        }


        log.debug("Encoding password for email: {}", request.getEmail());
        String hashPassword = passwordEncoder.encode(request.getPassword());
        log.debug("Password encoded successfully");

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(hashPassword)
                .role(Role.USER)
                .githubUsername(request.getGithubUsername())
                .leetcodeUsername(request.getLeetcodeUsername())
                .createdAt(LocalDateTime.now())
                .build();

        log.debug("Saving user to database: {}", user.getEmail());
        userRepository.save(user);
        log.info("User registered successfully with email: {}", request.getEmail());

        String token = jwtUtil.generateToken(user);
        Date expiryDate = new Date(System.currentTimeMillis() + jwtUtil.getExpirationMs());
        log.debug("JWT generated for user: {}, expires at: {}", user.getEmail(), expiryDate);

        return new AuthResponse(token, expiryDate, user.getUsername(), user.getRole().toString());
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for identifier: {}", request.getIdentifier());

        Optional<User> userOptional = userRepository.findByEmail(request.getIdentifier())
                .or(() -> userRepository.findByUsername(request.getIdentifier()));

        if (userOptional.isEmpty()) {
            log.warn("Login failed: No user found with identifier: {}", request.getIdentifier());
            throw new InvalidCredentialsException("No user found with this email or username");
        }

        User user = userOptional.get();
        log.debug("User found: {}", user.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for user: {}", request.getIdentifier());
            throw new InvalidCredentialsException("Invalid password");
        }

        log.debug("Password matched successfully for user: {}", user.getEmail());

        String token = jwtUtil.generateToken(user);
        Date expiryDate = new Date(System.currentTimeMillis() + jwtUtil.getExpirationMs());
        log.info("Login successful for user: {}", user.getEmail());
        log.debug("JWT issued, expires at: {}", expiryDate);

        return new AuthResponse(token, expiryDate, user.getUsername(), user.getRole().toString());
    }

    public ForgotUsernameResponse forgotUsername(ForgotUsernameRequest request) {
        log.info("Processing forgot username request for email: {}", request.getEmail());

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            log.warn("Forgot username failed: Email {} does not exist", request.getEmail());
            throw new EmailDoesNotExistsException("No user found by this email id");
        }

        User user = userOptional.get();
        log.info("Username found for email {}: {}", request.getEmail(), user.getUsername());

        return new ForgotUsernameResponse(user.getUsername());
    }

}