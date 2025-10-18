package com.ibs.user.domain;

import com.ibs.global.common.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", length = 36, nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password; // Nullable for social logins

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    private String jobTitle;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AuthProvider provider = AuthProvider.LOCAL; // LOCAL, GOOGLE, NAVER

    @Column
    private String providerId; // External provider user id (sub)

    // --- Business Methods ---
    public void updateDetails(String username, String name, String email, String phoneNumber, String jobTitle, String profileImage, UserStatus status) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.jobTitle = jobTitle;
        this.profileImage = profileImage;
        this.status = status;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateRole(Role newRole) {
        this.role = newRole;
    }
}

