package org._1mg.tt_backend.chat.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.MemberRepository;
import org._1mg.tt_backend.auth.entity.Member;
import org._1mg.tt_backend.chat.dto.ChatMessageDTO;
import org._1mg.tt_backend.chat.entity.ChatMessageEntity;
import org._1mg.tt_backend.chat.entity.ChatRoomEntity;
import org._1mg.tt_backend.chat.entity.UserChatEntity;
import org._1mg.tt_backend.chat.repository.ChatMessageRepository;
import org._1mg.tt_backend.chat.repository.ChatRoomRepository;
import org._1mg.tt_backend.chat.repository.UserChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserChatRepository userChatRepository;
    private final MemberRepository memberRepository;

    public List<ChatMessageEntity> getMessages(Integer chatroomId) {
        return chatMessageRepository.findByChatRoomChatroomId(chatroomId);
    }

//    public ChatRoomDTO getChatRoomByLandmarkId(Long landmarkId) {
//        // Landmark의 chatRoom 필드를 통해 ChatRoomEntity를 조회
//        ChatRoomEntity chatRoom = chatRoomRepository.findByLandmark_LandmarkId(landmarkId)
//                .orElseThrow(() -> new IllegalArgumentException("No chat room found for the given landmark ID"));
//
//        return ChatRoomDTO.builder()
//                .chatroomId(chatRoom.getChatroomId())
//                .landmarkName(chatRoom.getLandmark().getName())
//                .createdAt(chatRoom.getCreatedAt())

    /// /                .isActive(chatRoom.getIsActive())
//                .build();
//    }

    // 메세지 보내기
    public ChatMessageEntity saveMessage(ChatMessageDTO chatMessageDTO) {
        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatMessageDTO.getChatroomId())
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found with id: " + chatMessageDTO.getChatroomId()));

        // 멤버 찾기
        Member member = memberRepository.findById(UUID.fromString(chatMessageDTO.getMemberId()))
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        ChatMessageEntity message = ChatMessageEntity.create(chatRoom, member, chatMessageDTO.getContent());
        return chatMessageRepository.save(message);
    }

    // 메세지 읽음처리
    /*
    public void markMessageAsRead(Integer messageId, UUID memberId) {
        // 메시지 조회
        ChatMessageEntity message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with id: " + messageId));

        // 읽음 처리
        if (!message.getIsRead()) {
            message.setIsRead(true);
            chatMessageRepository.save(message);
            System.out.println("Message marked as read: " + messageId);
        }
    }


    public int countUnreadMessages(Integer chatroomId) {
        return chatMessageRepository.findByChatRoomChatroomIdAndIsReadFalse(chatroomId).size();
    }
     */

    // 입/퇴장
    public void userJoinChatRoom(Integer chatroomId, UUID memberId) {
        // 채팅방 찾기
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        // 멤버 찾기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // UserChatEntity 생성
        UserChatEntity userChat = UserChatEntity.create(chatRoom, member);
        userChatRepository.save(userChat);
    }

    public void userLeaveChatRoom(Integer chatroomId, UUID memberId) {
        // 가장 최근에 추가된 UserChatEntity 가져오기
        UserChatEntity userChat = userChatRepository.findFirstByChatRoomChatroomIdAndMember_MemberIdOrderByJoinedAtDesc(chatroomId, memberId);
        if (userChat == null) {
            throw new IllegalArgumentException("No active user found in chat room");
        }

        // 퇴장 시간 업데이트
        userChat.leave();
        userChatRepository.save(userChat);
    }
}
