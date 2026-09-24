package com.raajneel.Service.Implementation;

import com.raajneel.Configuration.JwtProvider;
import com.raajneel.Exception.UserException;
import com.raajneel.Mapper.UserMapper;
import com.raajneel.Model.PasswordResetToken;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.UserDTO;
import com.raajneel.Payload.Response.AuthResponse;
import com.raajneel.Repository.PasswordResetTokenRepository;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.AuthService;
import jakarta.transaction.Transactional;
import jdk.jshell.spi.ExecutionControl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;
    private final CustomUserServiceImpl customUserServiceImpl;
    // private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailServiceImpl emailServiceImpl;

    @Override
    public AuthResponse login(String username, String password) throws UserException {
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        User user = userRepository.findByEmail(username);

        // update the last login time
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setTitle("You've been logged in successfully");
        authResponse.setMessage("Welcome Back " + username);
        authResponse.setUser(UserMapper.toUserDTO(user));

        return authResponse;
    }

    private Authentication authenticate(String username, String password) throws UserException {
        UserDetails userDetails = customUserServiceImpl.loadUserByUsername(username);
        if(userDetails==null) {
            throw new UserException("User not found with email - " + password);
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new UserException("Wrong password");
        }
        return new UsernamePasswordAuthenticationToken(
                username, null, userDetails.getAuthorities());
    }

    @Override
    public AuthResponse signUp(UserDTO req) throws UserException {
        User user = userRepository.findByEmail(req.getEmail());

        if(user != null) {
            throw new UserException("Email id is already in use");
        }
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setPassword(passwordEncoder.encode(req.getPassword()));
        newUser.setFullName(req.getFullName());
        newUser.setRole(req.getRole());
        newUser.setPhone(req.getPhone());
        newUser.setLastLogin(req.getLastLogin());

        User savedUser = userRepository.save(newUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setTitle("Welcome " + newUser.getFullName());
        authResponse.setMessage("Registered Successfully");
        authResponse.setUser(UserMapper.toUserDTO(savedUser));
        return authResponse;
    }

    @Transactional
    public void createPasswordResetToken(String email) throws UserException {
        User user = userRepository.findByEmail(email);
        String frontendURL = "";
        if(user == null) {
            throw new UserException("User not found with given email");
        }
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();
        passwordResetTokenRepository.save(resetToken);
        String resetLink = frontendURL + token;
        String subject = "Password Reset Request";
        String body = "You have requested to reset your password. Use this link (valid for 5 minutes)";

        // sent email
        emailServiceImpl.sendEmail(user.getEmail(), subject, body);

    }

    @Transactional
    public void resetPassword(String token, String newPassword) throws Exception {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(()->new Exception("Token not valid"));

        if(resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new Exception("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }
}
