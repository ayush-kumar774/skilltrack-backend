package org.havoc.skilltrack.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.havoc.skilltrack.auth.entity.PasswordResetToken;
import org.havoc.skilltrack.auth.repository.PasswordResetTokenRepository;
import org.havoc.skilltrack.exception.CustomException.EmailDoesNotExistsException;
import org.havoc.skilltrack.exception.CustomException.InvalidCredentialsException;
import org.havoc.skilltrack.user.entity.User;
import org.havoc.skilltrack.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public String generateToken(String identifier) {
        log.info("Initiating password reset token generation for identifier: {}", identifier);

        Optional<User> userOptional = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier));

        if (userOptional.isEmpty()) {
            log.warn("Password reset token generation failed: No user found with identifier: {}", identifier);
            throw new EmailDoesNotExistsException("No user found with email or username: " + identifier);
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        log.debug("Generated UUID token: {}", token);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .email(user.getEmail())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        tokenRepository.save(resetToken);
        log.info("Password reset token saved successfully for user: {}", user.getEmail());

        return token;
    }

    public void resetPassword(String token, String newPassword) {
        log.info("Attempting password reset with token: {}", token);

        PasswordResetToken resetToken = tokenRepository.findByTokenAndUsedIsFalse(token)
                .orElseThrow(() -> {
                    log.warn("Invalid or already used token: {}", token);
                    return new InvalidCredentialsException("Invalid or expired token");
                });

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Token expired for email: {} at {}", resetToken.getEmail(), resetToken.getExpiresAt());
            throw new InvalidCredentialsException("Token has expired");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", resetToken.getEmail());
                    return new EmailDoesNotExistsException("User not found");
                });

        log.debug("Encoding new password for user: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user: {}", user.getEmail());

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        log.info("Reset token marked as used for email: {}", resetToken.getEmail());
    }
}