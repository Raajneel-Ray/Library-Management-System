package com.raajneel.Service.Implementation;

import com.raajneel.Exception.UserException;
import com.raajneel.Mapper.UserMapper;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.UserDTO;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("user not found!");
        }
        return user;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public User findById(Long id) throws UserException {
        return userRepository.findById(id).orElseThrow(
                () -> new UserException("User not found with given id")
        );
    }
}
