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
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Username already exists");
        }
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .jobTitle(request.jobTitle())
                .role(request.role())
                .status(request.status())
                .provider("LOCAL")
                .build();
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(PageRequest pageRequest) {
        Pageable pageable = pageRequest.of(); // Use the 'of()' method from the custom PageRequest
        Page<User> usersPage = userRepository.findAll(pageable);
        Page<UserResponse> userResponsesPage = usersPage.map(UserResponse::from);
        return new PageResponse<>(userResponsesPage); // Use the constructor that accepts a Page object
    }

    public void updateUserRole(UUID userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        user.updateRole(role);
        userRepository.save(user);
    }

    public void updateUser(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        user.setName(request.name());
        user.setJobTitle(request.jobTitle());
        user.setPhoneNumber(request.phoneNumber());
        userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        userRepository.delete(user);
    }
}
