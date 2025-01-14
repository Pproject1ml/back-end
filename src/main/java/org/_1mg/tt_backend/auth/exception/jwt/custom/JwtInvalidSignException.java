package org._1mg.tt_backend.auth.exception.jwt.custom;

public class JwtInvalidSignException extends CustomJwtException {

    public JwtInvalidSignException(String message) {
        super(message);
    }
}
