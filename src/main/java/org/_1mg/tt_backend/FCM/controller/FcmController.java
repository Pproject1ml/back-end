package org._1mg.tt_backend.FCM.controller;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.FCM.dto.FcmDTO;
import org._1mg.tt_backend.FCM.service.FcmService;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static org._1mg.tt_backend.base.CustomException.OK;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    //FCM 토큰 저장/갱신 API
    @PostMapping
    public ResponseDTO<String> registerFcmToken(Principal principal, @RequestBody FcmDTO fcmDTO) {

        fcmService.registerFcmToken(principal.getName(), fcmDTO);
        
        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .data(OK.getMessage())
                .build();
    }
}