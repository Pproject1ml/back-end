package org._1mg.tt_backend.auth.exception.member.custom;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
