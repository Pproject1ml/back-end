package org._1mg.tt_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org._1mg.tt_backend.chat.service.ChatService;
import org._1mg.tt_backend.chat.service.ChatroomService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org._1mg.tt_backend.base.CustomException.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatroomService chatroomService;

    @ResponseBody
    @GetMapping("/chat/list")
    public ResponseDTO<List<ChatroomDTO>> chatList(@RequestParam("id") Long profileId) {

        List<ChatroomDTO> result = chatroomService.getChatrooms(profileId);

        return ResponseDTO.<List<ChatroomDTO>>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(result)
                .build();
    }

    @ResponseBody
    @GetMapping("/chat/refresh")
    public ResponseDTO<List<TextDTO>> refresh(@RequestParam(name = "chatroom") Long chatroomId,
                                              @RequestParam(name = "start", required = false) Long startId,
                                              @RequestParam(name = "end", required = false) Long endId) {

        List<TextDTO> result = chatService.getMessagesByRange(chatroomId, startId, endId);

        return ResponseDTO.<List<TextDTO>>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(result)
                .build();
    }

    @MessageMapping("/message/{chatroomId}")
    @SendTo("/sub/room/{chatroomId}")
    public ResponseDTO<TextDTO> textMessage(@Payload TextDTO textDTO, @DestinationVariable Long chatroomId) {

        log.info("textDTO : {}", textDTO.toString());
        TextDTO text = chatService.sendText(textDTO, chatroomId);

        return ResponseDTO.<TextDTO>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(text)
                .build();
    }
}
