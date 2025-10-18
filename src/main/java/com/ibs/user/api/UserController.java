package com.ibs.user.api;

import com.ibs.global.dto.ApiResponse;
import com.ibs.global.dto.PageRequest;
import com.ibs.global.dto.PageResponse;
import com.ibs.user.dto.UserCreateRequest;
import com.ibs.user.dto.UserResponse;
import com.ibs.user.dto.UserRoleUpdateRequest;
import com.ibs.user.dto.UserUpdateRequest;
import com.ibs.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "사용자 API", description = "사용자 정보 조회 및 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 생성", description = "관리자가 새로운 사용자를 생성합니다 (ADMIN 권한 필요)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse userResponse = userService.createUser(request);
        return new ResponseEntity<>(ApiResponse.success(userResponse), HttpStatus.CREATED);
    }

    @Operation(summary = "사용자 목록 조회", description = "페이지/정렬로 사용자 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(PageRequest pageRequest) {
        PageResponse<UserResponse> users = userService.getAllUsers(pageRequest);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "사용자 권한 변경", description = "관리자가 지정 사용자의 권한을 변경합니다 (ADMIN 권한 필요)")
    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable UUID userId,
            @Valid @RequestBody UserRoleUpdateRequest request
    ) {
        userService.updateUserRole(userId, request.role());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "사용자 정보 수정", description = "관리자가 지정 사용자의 정보를 수정합니다 (ADMIN 권한 필요)")
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "사용자 삭제", description = "관리자가 지정 사용자를 삭제합니다 (ADMIN 권한 필요)")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

