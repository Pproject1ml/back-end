package org._1mg.tt_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;
import org._1mg.tt_backend.chat.dto.LocationDTO;
import org._1mg.tt_backend.chat.service.ChatroomService;
import org._1mg.tt_backend.landmark.service.LandmarkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org._1mg.tt_backend.base.CustomException.OK;

@RestController
@RequiredArgsConstructor
public class ChatroomController {

    private final ChatroomService chatroomService;
    private final LandmarkService landmarkService;

    @GetMapping("/chat/list")
    public ResponseDTO<List<ChatroomDTO>> chatList(@RequestParam("id") Long profileId) {

        List<ChatroomDTO> result = chatroomService.getChatrooms(profileId);

        return ResponseDTO.<List<ChatroomDTO>>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(result)
                .build();
    }

    @GetMapping("/chat/check-location")
    public ResponseDTO<String> checkLocation(@ModelAttribute LocationDTO locationDTO) {

        landmarkService.checkLocation(locationDTO);

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }
}