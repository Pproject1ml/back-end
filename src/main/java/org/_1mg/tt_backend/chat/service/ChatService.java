package org._1mg.tt_backend.chat.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.repository.MemberRepository;
import org._1mg.tt_backend.auth.repository.ProfileRepository;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.repository.ChatMessageRepository;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.chat.repository.MemberChatRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatroomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberChatRepository memberChatRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

//    public List<ChatMessageEntity> getMessages(Long chatroomId) {
//        return chatMessageRepository.findByChatroomChatroomId(chatroomId);
//    }

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

//    // 메세지 보내기
//    public ChatMessageEntity saveMessage(ChatMessageDTO chatMessageDTO) {
//        // 채팅방 찾기
//        ChatroomEntity chatRoom = chatRoomRepository.findById(chatMessageDTO.getChatroom().getChatroomId())
//                .orElseThrow(() -> new IllegalArgumentException("Chat room not found with id: " + chatMessageDTO.getChatroom().getChatroomId()));
//
//        // 멤버 찾기
//        Profile profile = memberRepository.findById(UUID.fromString(chatMessageDTO.getProfile().getProfileId()))
//                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
//
//        ChatMessageEntity message = ChatMessageEntity.create(chatRoom, member, chatMessageDTO.getContent());
//        return chatMessageRepository.save(message);
//    }

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

//    // 입/퇴장
//    public void userJoinChatRoom(Long chatroomId, UUID memberId) {
//        // 채팅방 찾기
//        ChatroomEntity chatRoom = chatRoomRepository.findById(chatroomId)
//                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
//
//        // 멤버 찾기
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
//
//        // UserChatEntity 생성
//        MemberChatEntity userChat = MemberChatEntity.create(chatRoom, member);
//        memberChatRepository.save(userChat);
//    }
//    public void userLeaveChatRoom(Long chatroomId, UUID memberId) {
//        // 가장 최근에 추가된 UserChatEntity 가져오기
//        MemberChatEntity userChat = memberChatRepository.findFirstByChatroomChatroomIdAndMemberMemberIdOrderByJoinedAtDesc(chatroomId, memberId);
//        if (userChat == null) {
//            throw new IllegalArgumentException("No active user found in chat room");
//        }
//
//        // 퇴장 시간 업데이트
//        userChat.leave();
//        memberChatRepository.save(userChat);
//    }
    public ChatroomDTO getParticipants(ChatroomEntity chatroom) {

        return chatroom.convertToDTO();


    }
}
