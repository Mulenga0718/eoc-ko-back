package com.ibs.user.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Login ID cannot be blank")
        @JsonProperty("loginId")
        @JsonAlias({"username", "email"})
        String loginId,

        @NotBlank(message = "Password cannot be blank")
        String password
) {
}
