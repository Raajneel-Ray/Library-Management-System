package com.raajneel.Mapper;

import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.UserDTO;
import org.springframework.stereotype.Service;

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
}
