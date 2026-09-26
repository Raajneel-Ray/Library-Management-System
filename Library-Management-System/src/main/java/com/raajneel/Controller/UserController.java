package com.raajneel.Controller;

import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.UserDTO;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return  ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUser() throws Exception {
        return  ResponseEntity.ok(userService.getCurrentUser());
    }
}
