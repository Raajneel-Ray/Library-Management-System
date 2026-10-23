package com.raajneel.Controller;

import com.raajneel.Exception.UserException;
import com.raajneel.Payload.DTO.UserDTO;
import com.raajneel.Payload.Request.ForgotPasswordRequest;
import com.raajneel.Payload.Request.LoginRequest;
import com.raajneel.Payload.Request.ResetPasswordRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.AuthResponse;
import com.raajneel.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/auth", "/api/auth"})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signupHandler(
            @RequestBody @Valid UserDTO req) throws UserException {
        AuthResponse response = authService.signUp(req);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(
            @RequestBody @Valid LoginRequest req) throws UserException {
        AuthResponse response = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPasswordHandler(
            @RequestBody @Valid ForgotPasswordRequest req) throws UserException {
        authService.createPasswordResetToken(req.getEmail());
        ApiResponse response = new ApiResponse(
                "A Reset link was sent to your email", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPasswordHandler(
            @RequestBody @Valid ResetPasswordRequest req) throws UserException {
        authService.resetPassword(req.getToken(), req.getNewPassword());
        ApiResponse response = new ApiResponse(
                "Password reset successful", true);
        return ResponseEntity.ok(response);
    }
}
