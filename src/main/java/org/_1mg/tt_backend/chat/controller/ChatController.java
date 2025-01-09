package org._1mg.tt_backend.chat.controller;

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

//    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
//    public ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO) {
//        System.out.println("Message received: " + chatMessageDTO);
//        ChatMessageEntity savedMessage = chatService.saveMessage(chatMessageDTO);
//
//        // 저장된 메시지를 DTO로 변환하여 클라이언트로 반환
//        ChatMessageDTO responseDTO = new ChatMessageDTO();
//        responseDTO.setMessageId(savedMessage.getMessageId());
//        responseDTO.setChatroomId(savedMessage.getChatRoom().getChatroomId());
//        responseDTO.setMemberId(String.valueOf(savedMessage.getMember().getMemberId()));
//        responseDTO.setContent(savedMessage.getContent());
//        responseDTO.setCreatedAt(savedMessage.getCreatedAt());
//        return responseDTO;
//    }
//
//
//    // 클릭 시 메세지 읽음처리
//    @MessageMapping("/read")
//    public void markAsRead(ChatMessageDTO chatMessageDTO) {
//        chatService.markMessageAsRead(chatMessageDTO.getMessageId(), chatMessageDTO.getMemberId());
//    }
//
//    @MessageMapping("/chat/enter")
//    @SendTo("/topic/messages")
//    public ChatMessageDTO enterChat(ChatMessageDTO chatMessageDTO) {
//        // 사용자 입장 처리
//        chatService.userJoinChatRoom(chatMessageDTO.getChatroomId(), UUID.fromString(chatMessageDTO.getMemberId()));
//
//        // 입장 메시지 생성
//        chatMessageDTO.setMessageType("ENTER");
//        chatMessageDTO.setContent(chatMessageDTO.getMemberId() + "님이 입장하셨습니다.");
//        return chatMessageDTO; // 브로드캐스트
//    }
//
//    @MessageMapping("/chat/leave")
//    @SendTo("/topic/messages")
//    public ChatMessageDTO leaveChat(ChatMessageDTO chatMessageDTO) {
//        // 사용자 퇴장 처리
//        chatService.userLeaveChatRoom(chatMessageDTO.getChatroomId(), UUID.fromString(chatMessageDTO.getMemberId()));
//        chatMessageDTO.setMessageType("LEAVE");
//        chatMessageDTO.setContent(chatMessageDTO.getMemberId() + "님이 퇴장하셨습니다.");
//        return chatMessageDTO;
//    }

// /pub/cache 로 메시지를 발행한다.
//    @MessageMapping("/")
//    @SendTo("/sub/room/{id}")
//    public void sendMessage(Map<String, Object> params) {
//
//        log.info("send message: {}", params);
//        // /sub/cache 에 구독중인 client에 메세지를 보낸다.
//        messagingTemplate.convertAndSend("/sub/cache/" + params.get("channelId"), params);
//    }

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

//    @MessageMapping("/join/{chatroomId}")
//    @SendTo("/sub/room/{chatroomId}")
//    public ResponseDTO<JoinDTO> joinMessage(@Payload JoinDTO joinDTO, @DestinationVariable Long chatroomId) {
//
//        JoinDTO join = chatService.joinChatroom(joinDTO, chatroomId);
//
//        return ResponseDTO.<JoinDTO>builder()
//                .status(OK.getStatus())
//                .message(OK.getMessage())
//                .data(join)
//                .build();
//    }

//    @MessageMapping("/enter/{chatroomId}")
//    @SendTo("/sub/room/{chatroomId}")
//    public ResponseDTO<String> enterMessage(@Payload EnterDTO enterDTO, @DestinationVariable Long chatroomId) {
//
//        //읽음 처리 및 읽음 카운트 기능 필요
//
//        return ResponseDTO.<String>builder()
//                .status(OK.getStatus())
//                .message(OK.getMessage())
//                .build();
//    }

    @MessageMapping("/text/{chatroomId}")
    @SendTo("/sub/room/{chatroomId}")
    public ResponseDTO<TextDTO> textMessage(@Payload TextDTO textDTO, @DestinationVariable Long chatroomId) {

        TextDTO text = chatService.sendText(textDTO, chatroomId);

        return ResponseDTO.<TextDTO>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(text)
                .build();
    }

//    @MessageMapping("/leave/{chatroomId}")
//    @SendTo("/sub/room/{chatroomId}")
//    public ResponseDTO<LeaveDTO> leaveMessage(@Payload LeaveDTO leaveDTO, @DestinationVariable Long chatroomId) {
//
//        //읽음 처리 및 읽음 카운트 기능 필요
//        LeaveDTO leave = chatService.leaveChatroom(leaveDTO, chatroomId);
//
//        return ResponseDTO.<LeaveDTO>builder()
//                .status(OK.getStatus())
//                .message(OK.getMessage())
//                .data(leave)
//                .build();
//    }
//
//    @MessageMapping("/die/{chatroomId}")
//    @SendTo("/sub/room/{chatroomId}")
//    public ResponseDTO<DieDTO> dieMessage(@Payload DieDTO dieDTO, @DestinationVariable Long chatroomId) {
//
//        //읽음 처리 및 읽음 카운트 기능 필요
//        chatService.dieChatroom(dieDTO, chatroomId);
//
//        return ResponseDTO.<DieDTO>builder()
//                .status(OK.getStatus())
//                .message(OK.getMessage())
//                .build();
//    }
}
