package com.ibs.user.api;

import com.ibs.user.dto.LoginRequest;
import com.ibs.user.dto.SignUpRequest;
import com.ibs.user.dto.TokenResponse;
import com.ibs.user.dto.RefreshTokenRequest;
import com.ibs.user.service.AuthService;
import com.ibs.global.dto.ApiResponse;
import com.ibs.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "인증 API", description = "사용자 회원가입 및 로그인 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "사용자 아이디와 비밀번호로 회원가입을 요청합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = Long.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력 값 (아이디/비밀번호 형식 오류 또는 중복된 아이디)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UUID>> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        UUID userId = authService.signup(signUpRequest);
        return new ResponseEntity<>(ApiResponse.success(userId), HttpStatus.CREATED);
    }

    @Operation(summary = "로그인", description = "사용자 아이디와 비밀번호로 로그인을 요청하고, 성공 시 액세스 토큰과 리프레시 토큰을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (아이디 또는 비밀번호 불일치)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 갱신 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않거나 만료된 리프레시 토큰)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        TokenResponse tokenResponse = authService.refresh(refreshTokenRequest.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}
