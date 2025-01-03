package org._1mg.tt_backend.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.ChatMessageDTO;
import org._1mg.tt_backend.chat.dto.ChatRoomDTO;
import org._1mg.tt_backend.chat.entity.ChatMessageEntity;
import org._1mg.tt_backend.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Slf4j
@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 랜드마크와 연결
    @GetMapping
    public ResponseDTO<ChatRoomDTO> getChatRoomByLandmark(@RequestParam("landmarkId") Long landmarkId) {
        ChatRoomDTO chatRoomDTO = chatService.getChatRoomByLandmarkId(landmarkId);

        return ResponseDTO.<ChatRoomDTO>builder()
                .status(200)
                .message("CHAT ROOM FOR LANDMARK ID " + landmarkId)
                .data(chatRoomDTO)
                .build();
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "chat"; // templates/chat.html 파일을 렌더링
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO) {
        System.out.println("Message received: " + chatMessageDTO);
        ChatMessageEntity savedMessage = chatService.saveMessage(chatMessageDTO);

        // 저장된 메시지를 DTO로 변환하여 클라이언트로 반환
        ChatMessageDTO responseDTO = new ChatMessageDTO();
        responseDTO.setMessageId(savedMessage.getMessageId());
        responseDTO.setChatroomId(savedMessage.getChatRoom().getChatroomId());
        responseDTO.setMemberId(String.valueOf(savedMessage.getMember().getMemberId()));
        responseDTO.setContent(savedMessage.getContent());
        responseDTO.setCreatedAt(savedMessage.getCreatedAt());
        return responseDTO;
    }


    // 클릭 시 메세지 읽음처리
    /*
    @MessageMapping("/read")
    public void markAsRead(ChatMessageDTO chatMessageDTO) {
        chatService.markMessageAsRead(chatMessageDTO.getMessageId(), chatMessageDTO.getMemberId());
    }
     */

    @MessageMapping("/chat/enter")
    @SendTo("/topic/messages")
    public ChatMessageDTO enterChat(ChatMessageDTO chatMessageDTO) {
        // 사용자 입장 처리
        chatService.userJoinChatRoom(chatMessageDTO.getChatroomId(), UUID.fromString(chatMessageDTO.getMemberId()));

        // 입장 메시지 생성
        chatMessageDTO.setMessageType("ENTER");
        chatMessageDTO.setContent(chatMessageDTO.getMemberId() + "님이 입장하셨습니다.");
        return chatMessageDTO; // 브로드캐스트
    }

    @MessageMapping("/chat/leave")
    @SendTo("/topic/messages")
    public ChatMessageDTO leaveChat(ChatMessageDTO chatMessageDTO) {
        // 사용자 퇴장 처리
        chatService.userLeaveChatRoom(chatMessageDTO.getChatroomId(), UUID.fromString(chatMessageDTO.getMemberId()));
        chatMessageDTO.setMessageType("LEAVE");
        chatMessageDTO.setContent(chatMessageDTO.getMemberId() + "님이 퇴장하셨습니다.");
        return chatMessageDTO;
    }
}
