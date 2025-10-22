package com.ibs;

import com.ibs.donation.config.TossPaymentsProperties;
import com.ibs.user.domain.Role;
import com.ibs.user.domain.User;
import com.ibs.user.domain.UserStatus;
import com.ibs.user.repository.UserRepository;
import com.ibs.user.security.JwtProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, TossPaymentsProperties.class})
@EnableCaching
//@EnableJpaAuditing
public class IbsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IbsBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User adminUser = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .name("admin")
                        .email("admin@example.com")
                        .phoneNumber(null)
                        .jobTitle("관리자")
                        .role(Role.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .build();
                userRepository.save(adminUser);
                System.out.println("Admin user created: admin/admin");
            }
        };
    }
}

