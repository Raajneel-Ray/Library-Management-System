package com.raajneel.Service;

import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.UserDTO;

import java.util.List;

public interface UserService {

    User getCurrentUser() throws Exception;

    List<UserDTO> getAllUsers();
}
