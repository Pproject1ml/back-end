package org._1mg.tt_backend.auth.entity;

import lombok.Getter;

@Getter
public enum Gender {


    male("male"), //가입
    female("female");

    private final String value;

    Gender(String value) {
        this.value = value;
    }
}
