package com.ibs.user.security;

import com.ibs.user.domain.Role;
import com.ibs.user.domain.User;
import com.ibs.user.domain.UserStatus;
import com.ibs.user.service.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private final JwtProperties jwtProperties;

    private Key key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.tokenValidityInSeconds());
    }

    public String createRefreshToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.refreshTokenValidityInSeconds());
    }

    public String generateToken(Authentication authentication, long validityInSeconds) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInSeconds * 1000);

        String authorities = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("id", user.getId() != null ? user.getId().toString() : null);
        userClaims.put("loginId", user.getUsername());
        userClaims.put("name", user.getName());
        userClaims.put("email", user.getEmail());
        userClaims.put("phoneNumber", user.getPhoneNumber());
        userClaims.put("jobTitle", user.getJobTitle());
        userClaims.put("profileImage", user.getProfileImage());
        userClaims.put("role", user.getRole() != null ? user.getRole().name() : null);
        userClaims.put("status", user.getStatus() != null ? user.getStatus().name() : null);
        userClaims.put("provider", user.getProvider());
        userClaims.put("providerId", user.getProviderId());

        String userId = customUserDetails.getId() != null ? customUserDetails.getId().toString() : null;

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", userId)
                .claim("role", user.getRole() != null ? user.getRole().name() : null)
                .claim("email", user.getEmail())
                .claim("profileImage", user.getProfileImage())
                .claim("name", user.getName())
                .claim("jobTitle", user.getJobTitle())
                .claim("phoneNumber", user.getPhoneNumber())
                .claim("status", user.getStatus() != null ? user.getStatus().name() : null)
                .claim("provider", user.getProvider())
                .claim("providerId", user.getProviderId())
                .claim("auth", authorities)
                .claim("user", userClaims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth", String.class).split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Map<String, Object> userAttributes = new HashMap<>();
        Object userClaim = claims.get("user");
        if (userClaim instanceof Map<?, ?> map) {
            map.forEach((key, value) -> userAttributes.put(String.valueOf(key), value));
        }

        UUID userId = parseUuid(getAttribute(userAttributes, "id", claims.get("userId", String.class)));
        String username = getAttribute(userAttributes, "loginId", claims.getSubject());
        String name = getAttribute(userAttributes, "name", claims.get("name", String.class));
        String email = getAttribute(userAttributes, "email", claims.get("email", String.class));
        String profileImage = getAttribute(userAttributes, "profileImage", claims.get("profileImage", String.class));
        String jobTitle = getAttribute(userAttributes, "jobTitle", claims.get("jobTitle", String.class));
        String phoneNumber = getAttribute(userAttributes, "phoneNumber", claims.get("phoneNumber", String.class));
        String provider = getAttribute(userAttributes, "provider", claims.get("provider", String.class));
        String providerId = getAttribute(userAttributes, "providerId", claims.get("providerId", String.class));
        String roleValue = getAttribute(userAttributes, "role", claims.get("role", String.class));
        String statusValue = getAttribute(userAttributes, "status", claims.get("status", String.class));

        User user = User.builder()
                .id(userId)
                .username(username)
                .name(name)
                .email(email)
                .profileImage(profileImage)
                .jobTitle(jobTitle)
                .phoneNumber(phoneNumber)
                .provider(provider)
                .providerId(providerId)
                .role(parseRole(roleValue))
                .status(parseStatus(statusValue))
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(customUserDetails, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return false;
    }

    private UUID parseUuid(String value) {
        if (!hasText(value)) {
            return null;
        }
        return UUID.fromString(value);
    }

    private Role parseRole(String value) {
        if (!hasText(value)) {
            return null;
        }
        return Role.valueOf(value.toUpperCase(Locale.ROOT));
    }

    private UserStatus parseStatus(String value) {
        if (!hasText(value)) {
            return null;
        }
        return UserStatus.valueOf(value.toUpperCase(Locale.ROOT));
    }

    private String getAttribute(Map<String, Object> attributes, String key, String fallback) {
        Object value = attributes.get(key);
        if (value == null) {
            return fallback;
        }
        String text = value.toString();
        return hasText(text) ? text : fallback;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
