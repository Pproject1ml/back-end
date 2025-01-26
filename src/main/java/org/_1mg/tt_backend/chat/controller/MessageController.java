package org._1mg.tt_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.EnterDTO;
import org._1mg.tt_backend.chat.dto.LeaveDTO;
import org._1mg.tt_backend.chat.dto.RefreshDTO;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org._1mg.tt_backend.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org._1mg.tt_backend.base.CustomException.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @ResponseBody
    @GetMapping("/chat/refresh")
    public ResponseDTO<List<TextDTO>> refresh(@ModelAttribute RefreshDTO refreshDTO) {

        List<TextDTO> result = messageService.getMessagesByRange(refreshDTO);

        return ResponseDTO.<List<TextDTO>>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(result)
                .build();
    }

    @MessageMapping("/enter/{chatroomId}")
    @SendTo("/sub/room/{chatroomId}")
    public ResponseDTO<String> enterMessage(@Payload EnterDTO enterDTO, @DestinationVariable Long chatroomId) {

        /*
            채팅읽음처리 관련 기능
         */

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }

    @MessageMapping("/message/{chatroomId}")
    @SendTo("/sub/room/{chatroomId}")
    public void textMessage(@Payload TextDTO textDTO, @DestinationVariable Long chatroomId) {

        List<TextDTO> texts = messageService.sendText(textDTO);

        for (TextDTO text : texts) {
            messagingTemplate.convertAndSend("/sub/room/" + chatroomId,
                    ResponseDTO.<TextDTO>builder()
                            .status(OK.getStatus())
                            .message(OK.getMessage())
                            .data(text)
                            .build());
        }
    }

    @MessageMapping("/leave/{chatroomId}")
    @SendTo("/sub/room/{chatroomId}")
    public ResponseDTO<String> leaveMessage(@Payload LeaveDTO leaveDTO, @DestinationVariable Long chatroomId) {


        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }
}
