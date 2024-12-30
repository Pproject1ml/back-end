package org._1mg.tt_backend.exception.auth;


public class JwtExpiredTokenException extends CustomJwtException {

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }
}
