package org._1mg.tt_backend.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org._1mg.tt_backend.base.CustomException.ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseDTO<String> exception(Exception e) {
        log.error(e.getMessage());

        return ResponseDTO.<String>builder()
                .status(ERROR.getStatus())
                .message(ERROR.getMessage())
                .build();
    }
}
