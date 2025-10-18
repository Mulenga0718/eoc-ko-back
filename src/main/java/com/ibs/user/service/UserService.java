package com.ibs.user.service;

import com.ibs.global.dto.PageRequest;
import com.ibs.global.dto.PageResponse;
import com.ibs.global.exception.BusinessException;
import com.ibs.global.exception.ErrorCode;
import com.ibs.user.domain.Role;
import com.ibs.user.domain.User;
import com.ibs.user.dto.UserCreateRequest;
import com.ibs.user.dto.UserResponse;
import com.ibs.user.dto.UserUpdateRequest;
import com.ibs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTITY, "Username already exists");
        });

        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTITY, "Email already exists");
        });

        User user = User.builder()
                .username(request.username())
                .password(request.password() != null ? passwordEncoder.encode(request.password()) : null)
                .name(request.name())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .jobTitle(request.jobTitle())
                .role(request.role())
                .status(request.status())
                .build();

        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    public PageResponse<UserResponse> getAllUsers(PageRequest pageRequest) {
        Pageable pageable = pageRequest.of();
        Page<User> usersPage = userRepository.findAll(pageable);
        return new PageResponse<>(usersPage.map(UserResponse::from));
    }

    @Transactional
    public void updateUserRole(UUID userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "User not found"));
        user.updateRole(newRole);
    }

    @Transactional
    public void updateUser(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "User not found"));

        user.updateDetails(
                request.username(),
                request.name(),
                request.email(),
                request.phoneNumber(),
                request.jobTitle(),
                request.profileImage(),
                request.status()
        );

        if (request.password() != null && !request.password().isEmpty()) {
            user.updatePassword(passwordEncoder.encode(request.password()));
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "User not found"));
        userRepository.delete(user);
    }
}

