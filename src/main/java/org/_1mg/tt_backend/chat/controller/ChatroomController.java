package org._1mg.tt_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.LocationDTO;
import org._1mg.tt_backend.chat.service.ChatroomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import static org._1mg.tt_backend.base.CustomException.OK;

@RestController
@RequiredArgsConstructor
public class ChatroomController {

    private final ChatroomService chatroomService;

    @GetMapping("/chat/check-location")
    public ResponseDTO<String> checkLocation(@ModelAttribute LocationDTO locationDTO) {

        chatroomService.checkLocation(locationDTO);

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }
}
