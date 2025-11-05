package com.hkb.portfolio_backend.config;

import com.hkb.portfolio_backend.entity.User;
import com.hkb.portfolio_backend.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);



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
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminUsername + "@example.com");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);
            log.info("Created default admin user: {}", adminUsername);



        }
        else {
            log.info("Admin already exits");

        }
    }
}