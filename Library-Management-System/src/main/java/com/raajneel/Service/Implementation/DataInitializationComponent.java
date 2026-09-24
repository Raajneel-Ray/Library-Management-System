package com.raajneel.Service.Implementation;

import com.raajneel.Domain.UserRole;
import com.raajneel.Model.User;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private  final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        String adminEmail = "raajneelray@gmail.com";
        String adminPassword = "Welcome@12345";

        if(userRepository.findByEmail(adminEmail) == null) {
            User user = User.builder()
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .fullName("Raajneel Ray")
                    .role(UserRole.ROLE_ADMIN)
                    .build();
            User admin = userRepository.save(user);
            System.out.println("Default admin user created: " + adminEmail);
        }
    }
}
