package com.raajneel.Mapper;

import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.UserDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserMapper {
    public static UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setLastLogin(user.getLastLogin());
        userDTO.setPhone(user.getPhone());

        return  userDTO;

    }

    public static List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    public static Set<UserDTO>  toDTOSet(Set<User> users) {
        return users.stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toSet());
    }

    public static User toEntity(UserDTO userDTO) {
        User createdUser = new User();
        createdUser.setFullName(userDTO.getFullName());
        createdUser.setEmail(userDTO.getEmail());
        createdUser.setRole(userDTO.getRole());
        createdUser.setPhone(userDTO.getPhone());
        createdUser.setLastLogin(userDTO.getLastLogin());
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setPassword(userDTO.getPassword());
        return createdUser;
    }
}
