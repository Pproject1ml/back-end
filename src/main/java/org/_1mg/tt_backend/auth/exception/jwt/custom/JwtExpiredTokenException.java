package org._1mg.tt_backend.auth.exception.jwt.custom;


public class JwtExpiredTokenException extends CustomJwtException {

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }
}
