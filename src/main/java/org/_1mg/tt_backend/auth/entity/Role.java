package org._1mg.tt_backend.auth.entity;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_USER("USER");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public static Role getRole(String input) {

        for (Role role : Role.values()) {
            if (role.name().equals(input)) {
                return role;
            }
        }

        return null;
    }
}
