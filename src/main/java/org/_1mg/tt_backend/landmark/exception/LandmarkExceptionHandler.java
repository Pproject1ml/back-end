package org._1mg.tt_backend.landmark.exception;

import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(basePackages = "org._1mg.tt_backend.landmark") // 특정 패키지로 제한
@RestController
public class LandmarkExceptionHandler {

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                ResponseDTO.<String>builder()
                        .status(HttpStatus.BAD_REQUEST.value()) // 400 상태 코드
                        .message(ex.getMessage()) // 예외 메시지
                        .data(null)
                        .build()
        );
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<String>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResponseDTO.<String>builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // 500 상태 코드
                        .message("Landmark 도메인에서 오류 발생: " + ex.getMessage())
                        .data(null)
                        .build()
        );
    }
}
