package org._1mg.tt_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.*;
import org._1mg.tt_backend.chat.service.ChatroomService;
import org._1mg.tt_backend.landmark.service.LandmarkService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org._1mg.tt_backend.base.CustomException.OK;

@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
public class ChatroomController {

    private final ChatroomService chatroomService;
    private final LandmarkService landmarkService;

    @PostMapping("/join")
    public ResponseDTO<String> join(@RequestBody JoinDTO joinDTO) {


        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }


    @GetMapping("/list")
    public ResponseDTO<List<ChatroomDTO>> chatList(@RequestParam("id") Long profileId) {

        List<ChatroomDTO> result = chatroomService.getChatrooms(profileId);

        return ResponseDTO.<List<ChatroomDTO>>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(result)
                .build();
    }

    @GetMapping("/check-location")
    public ResponseDTO<String> checkLocation(@ModelAttribute LocationDTO locationDTO) {

        landmarkService.checkLocation(locationDTO);

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }

    @PostMapping("/alarm")
    public ResponseDTO<String> setAlarm(@RequestBody AlarmDTO alarmDTO) {

        chatroomService.changeAlarm(alarmDTO);

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }

    @MessageMapping("/join/{chatroomId}")
    public ResponseDTO<String> joinMessage(@Payload JoinDTO joinDTO, @DestinationVariable Long chatroomId) {

        String nickname = chatroomService.joinChatroom(joinDTO);
        String message = nickname + "님이 채팅방에 입장하셨습니다";

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(message)
                .build();
    }

    @MessageMapping("/die/{chatroomId}")
    @SendTo("/sub/room/{chatroomId}")
    public ResponseDTO<String> dieMessage(@Payload DieDTO dieDTO, @DestinationVariable Long chatroomId) {


        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }
}
