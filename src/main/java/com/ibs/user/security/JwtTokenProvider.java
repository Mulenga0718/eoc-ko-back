package com.ibs.user.security;

import com.ibs.user.domain.Role;
import com.ibs.user.service.CustomUserDetails;
import io.jsonwebtoken.*;
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

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInSeconds * 1000);

        String authorities = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .claim("userId", customUserDetails.getId().toString())
                .claim("role", customUserDetails.getUser().getRole().name())
                .claim("email", customUserDetails.getUser().getEmail())
                .claim("profileImage", customUserDetails.getUser().getProfileImage())
                .claim("name", customUserDetails.getUser().getName())
                .claim("jobTitle", customUserDetails.getUser().getJobTitle())
                .claim("auth", authorities)
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

        UUID userId = UUID.fromString(claims.get("userId", String.class));
        String username = claims.getSubject();
        String roleStr = claims.get("role", String.class);
        Role role = roleStr != null ? Role.valueOf(roleStr) : null;
        String email = claims.get("email", String.class);
        String profileImage = claims.get("profileImage", String.class);
        String name = claims.get("name", String.class);
        String jobTitle = claims.get("jobTitle", String.class);
        String auth = claims.get("auth", String.class);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(auth.split(","))
                        .map(s -> s.startsWith("ROLE_") ? s : "ROLE_" + s)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        CustomUserDetails customUserDetails = new CustomUserDetails(userId, username, name, email, profileImage, jobTitle, role, authorities);

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
}
