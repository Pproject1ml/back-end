package org._1mg.tt_backend.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;
import org._1mg.tt_backend.chat.dto.MessageDTO;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org._1mg.tt_backend.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org._1mg.tt_backend.exception.CustomException.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    @ResponseBody
    @GetMapping("/chat/list")
    public ResponseDTO<List<ChatroomDTO>> chatList(@RequestParam("id") Long profileId) {

        List<ChatroomDTO> result = chatService.getChatrooms(profileId);

        return ResponseDTO.<List<ChatroomDTO>>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(result)
                .build();
    }

    @ResponseBody
    @GetMapping("/chat/refresh")
    public ResponseDTO<List<MessageDTO>> refresh(@RequestParam("chatroom") Long chatroomId, @RequestParam("start") Long startId, @RequestParam("end") Long endId) {

        List<MessageDTO> result = chatService.getMessagesByRange(chatroomId, startId, endId);

        return ResponseDTO.<List<MessageDTO>>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(result)
                .build();
    }

    @Operation(
            summary = "STOMP 소켓을 통한 chat",
            description = "메세지 전송 URL : /pub/message/{chatroomId}\n" +
                    "구독 URL : /sub/room/{chatroomId}"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 로그인 - body.status : 20"),
    })
    @MessageMapping("/message/{chatroomId}")
    @SendTo("/sub/room/{chatroomId}")
    public ResponseDTO<TextDTO> textMessage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON BODY",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ " + '\"' + "profileId" + '\"' + ":" + '\"' + "value" + '\"' + "}")
                    )
            ) @Payload TextDTO textDTO, @DestinationVariable Long chatroomId) {

        TextDTO text = chatService.sendText(textDTO, chatroomId);

        return ResponseDTO.<TextDTO>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(text)
                .build();
    }
}
