package org.havoc.skilltrack.auth.repository;

import org.havoc.skilltrack.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByTokenAndUsedIsFalse(String token);
    void deleteAllByExpiresAtBefore(LocalDateTime time);
}

