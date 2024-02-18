package com.cos.jwt.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Roles {
    USER("user", "ROLE_USER"),
    ADMIN("admin", "ROLE_ADMIN");
    private final String key;
    private final String value;

}
