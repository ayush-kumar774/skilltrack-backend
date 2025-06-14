package org.havoc.skilltrack.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.havoc.skilltrack.auth.dto.*;
import org.havoc.skilltrack.auth.service.AuthService;
import org.havoc.skilltrack.auth.service.PasswordResetService;
import org.havoc.skilltrack.common.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login with email or username", description = "Returns JWT and user info")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-username")
    public ResponseEntity<ForgotUsernameResponse> handleForgotUsername(@Valid @RequestBody ForgotUsernameRequest request) {
        ForgotUsernameResponse response = authService.forgotUsername(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password-request")
    public ResponseEntity<PasswordResetResponse> forgotPasswordRequest(@Valid @RequestBody ForgotPasswordRequest request) {
        String token = passwordResetService.generateToken(request.getIdentifier());
        log.info("Password reset token: {}", token);
        String message = "Password reset link has been sent to your email";
        PasswordResetResponse passwordResetResponse = new PasswordResetResponse(message, token);
//        return ResponseEntity.ok(new MessageResponse(message));
        return ResponseEntity.ok(passwordResetResponse);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
    }



}
