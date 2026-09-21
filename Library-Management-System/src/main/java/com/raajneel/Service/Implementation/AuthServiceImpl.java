package com.raajneel.Service.Implementation;

import com.raajneel.Configuration.JwtProvider;
import com.raajneel.Exception.UserException;
import com.raajneel.Mapper.UserMapper;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.UserDTO;
import com.raajneel.Payload.Response.AuthResponse;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.AuthService;
import jdk.jshell.spi.ExecutionControl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;


    @Override
    public AuthResponse login(String username, String password) {
        return null;
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

    @Override
    public void createPasswordResetToken(String email) {

    }

    @Override
    public void resetPassword(String token, String newPassword) {

    }
}
