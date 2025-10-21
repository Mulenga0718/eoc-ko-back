package com.ibs.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CloudflareResponseDto(
        @JsonProperty("success") boolean success,
        @JsonProperty("result") Result result
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            @JsonProperty("id") String id,
            @JsonProperty("variants") List<String> variants
    ) {
    }
}
