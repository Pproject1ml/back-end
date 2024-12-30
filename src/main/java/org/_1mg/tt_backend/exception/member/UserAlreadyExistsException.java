package org._1mg.tt_backend.exception.member;


import lombok.Getter;

/**
 * AuthenticationException 으로 처리되면 안 됨
 * 별도의 ControllerAdvice, ExceptionHandler 를 구현해야 함
 **/
@Getter
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
