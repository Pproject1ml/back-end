package org._1mg.tt_backend.landmark.exception;

import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.CustomException;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "org._1mg.tt_backend.landmark") // 특정 패키지로 제한
public class LandmarkExceptionHandler {

    // IllegalArgumentException 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseDTO<String> handleIllegalArgumentException(IllegalArgumentException ex) {

        return ResponseDTO.<String>builder()
                .status(CustomException.LANDMARK_ILLEGAL_ARGUMENT.getStatus()) // 커스텀 상태 코드
                .message(CustomException.LANDMARK_ILLEGAL_ARGUMENT.getMessage()) // 커스텀 메시지
                .build();
    }

    // 기타 예외 처리
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseDTO<String> handleGeneralException(Exception ex) {

        log.error("랜드마크 관련 예외 : {}", ex.getMessage());
        return ResponseDTO.<String>builder()
                .status(CustomException.LANDMARK_GENERAL_ERROR.getStatus()) // 커스텀 상태 코드
                .message(CustomException.LANDMARK_GENERAL_ERROR.getMessage()) // 커스텀 메시지
                .build();
    }
}
