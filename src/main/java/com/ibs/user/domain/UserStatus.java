package com.ibs.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    LEAVE("휴면"),
    WITHDRAWN("탈퇴");

    private final String description;
}
