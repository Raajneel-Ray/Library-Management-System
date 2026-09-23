package com.raajneel.Service;

import com.raajneel.Exception.UserException;
import com.raajneel.Payload.DTO.UserDTO;
import com.raajneel.Payload.Response.AuthResponse;

public interface AuthService {

    AuthResponse login(String username, String password) throws UserException;

    AuthResponse signUp(UserDTO user) throws UserException;

    void createPasswordResetToken(String email) throws UserException;

    void resetPassword(String token, String newPassword) throws Exception;
}
