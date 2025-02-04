package org._1mg.tt_backend.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org._1mg.tt_backend.base.CustomException.ERROR;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseDTO<String> exception(Exception e) {
        log.error(e.getMessage());

        return ResponseDTO.<String>builder()
                .status(ERROR.getStatus())
                //.message(ERROR.getMessage())
                .message(e.getMessage())
                .build();
    }
}
