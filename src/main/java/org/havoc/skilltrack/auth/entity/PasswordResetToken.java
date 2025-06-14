package org.havoc.skilltrack.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    private String token; // UUID or random string

    @Email
    @NotBlank
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private boolean used;
}
