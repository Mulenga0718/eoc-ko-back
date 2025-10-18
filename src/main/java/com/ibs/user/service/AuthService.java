package com.ibs.user.service;

import com.ibs.global.exception.BusinessException;
import com.ibs.global.exception.ErrorCode;
import com.ibs.user.domain.RefreshToken;
import com.ibs.user.domain.Role;
import com.ibs.user.domain.User;
import com.ibs.user.domain.UserStatus;
import com.ibs.user.dto.LoginRequest;
import com.ibs.user.dto.SignUpRequest;
import com.ibs.user.dto.TokenResponse;
import com.ibs.user.repository.RefreshTokenRepository;
import com.ibs.user.repository.UserRepository;
import com.ibs.user.security.JwtProperties;
import com.ibs.user.security.JwtTokenProvider;
import com.ibs.user.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

    public UUID signup(SignUpRequest signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.username()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        if (signUpRequest.password() == null || signUpRequest.password().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Password cannot be empty.");
        }

        User user = userMapper.toUser(signUpRequest);
        user.updatePassword(passwordEncoder.encode(signUpRequest.password()));
        user.updateRole(signUpRequest.role() != null ? signUpRequest.role() : Role.MEMBER);
        user.updateDetails(
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getJobTitle(),
                null,
                UserStatus.ACTIVE
        );

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public TokenResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(authentication);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = customUserDetails.getId();

        refreshTokenRepository.findByUserId(userId).ifPresentOrElse(
                (refreshToken) -> refreshToken.updateToken(refreshTokenValue, Instant.now().plusSeconds(jwtProperties.refreshTokenValidityInSeconds())),
                () -> refreshTokenRepository.save(RefreshToken.builder()
                        .userId(userId)
                        .token(refreshTokenValue)
                        .expiryDate(Instant.now().plusSeconds(jwtProperties.refreshTokenValidityInSeconds()))
                        .build())
        );

        return new TokenResponse(accessToken, refreshTokenValue);
    }

    public TokenResponse refresh(String refreshTokenValue) {
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Authentication authentication = createAuthentication(user);
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        refreshToken.updateToken(newRefreshToken, Instant.now().plusSeconds(jwtProperties.refreshTokenValidityInSeconds()));

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    private Authentication createAuthentication(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }
}

