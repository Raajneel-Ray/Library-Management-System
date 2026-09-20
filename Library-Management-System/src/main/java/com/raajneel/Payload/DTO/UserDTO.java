package com.raajneel.Payload.DTO;

import com.raajneel.Domain.AuthProvider;
import com.raajneel.Domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String password;
    private String fullName;
    private UserRole role;
    private String phone;
    private String userName;
    private LocalDateTime lastLogin;

}
