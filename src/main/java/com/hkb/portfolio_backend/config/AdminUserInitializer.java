package com.hkb.portfolio_backend.config;

import com.hkb.portfolio_backend.entity.User;
import com.hkb.portfolio_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    public AdminUserInitializer(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                AppProperties appProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        // Use env vars as fallback; AppProperties could carry admin defaults if you want
        String adminUsername = System.getenv().getOrDefault("ADMIN_USERNAME", "admin");
        String adminPassword = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin123"); // change in prod

        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            System.out.println("admin user env: " + adminUsername+ "/"+adminPassword);
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminUsername + "@example.com");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);
            System.out.println("Created default admin user: " + adminUsername);


        }
        else {
            System.out.println("Admin already exits");
        }
    }
}