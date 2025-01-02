package org._1mg.tt_backend.chat;

import jakarta.transaction.Transactional;
import org._1mg.tt_backend.auth.MemberRepository;
import org._1mg.tt_backend.auth.entity.Member;
import org._1mg.tt_backend.chat.dto.ChatMessageDTO;
import org._1mg.tt_backend.chat.entity.ChatMessageEntity;
import org._1mg.tt_backend.chat.entity.ChatRoomEntity;
import org._1mg.tt_backend.chat.entity.UserChatEntity;
import org._1mg.tt_backend.chat.repository.ChatMessageRepository;
import org._1mg.tt_backend.chat.repository.ChatRoomRepository;
import org._1mg.tt_backend.chat.repository.UserChatRepository;
import org._1mg.tt_backend.chat.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChatServiceUnitTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserChatRepository userChatRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testUserJoinChatRoom() {
        // Given: 테스트 데이터를 준비
        Integer chatroomId = 1; // 채팅방 ID
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatroomId).orElseThrow(); // 채팅방 가져오기
        Member member = memberRepository.save( // 새로운 사용자를 저장
                Member.builder()
                        .nickname("testUser") // 사용자 이름
                        .email("test@example.com") // 사용자 이메일
                        .oauthId("oauthTestId") // OAuth ID
                        .oauthProvider("google") // OAuth 제공자
                        .build()
        );

        // When: 사용자가 채팅방에 입장
        chatService.userJoinChatRoom(chatRoom.getChatroomId(), member.getMemberId());

        // Then: 데이터베이스에서 해당 사용자의 입장 정보를 확인
        UserChatEntity userChat = userChatRepository.findAllByChatRoomChatroomId(chatRoom.getChatroomId())
                .stream()
                .filter(chat -> chat.getMember().getMemberId().equals(member.getMemberId()))
                .findFirst()
                .orElse(null);

        // 검증: 데이터가 올바르게 저장되었는지 확인
        assertNotNull(userChat); // 입장 정보가 존재해야 함
        assertEquals(chatRoom.getChatroomId(), userChat.getChatRoom().getChatroomId()); // 채팅방 ID가 일치해야 함
        assertEquals(member.getMemberId(), userChat.getMember().getMemberId()); // 사용자 ID가 일치해야 함
        assertNotNull(userChat.getJoinedAt()); // 입장 시간이 기록되어야 함
    }

    @Test
    void testUserLeaveChatRoom() {
        // Given: 테스트 데이터를 준비
        Integer chatroomId = 1; // 채팅방 ID
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatroomId).orElseThrow(); // 채팅방 가져오기
        Member member = memberRepository.save( // 새로운 사용자를 저장
                Member.builder()
                        .nickname("testUser")
                        .email("test@example.com")
                        .oauthId("oauthTestId")
                        .oauthProvider("google")
                        .build()
        );

        // 사용자가 채팅방에 입장
        chatService.userJoinChatRoom(chatRoom.getChatroomId(), member.getMemberId());

        // When: 사용자가 채팅방에서 퇴장
        chatService.userLeaveChatRoom(chatRoom.getChatroomId(), member.getMemberId());

        // Then: 데이터베이스에서 사용자의 퇴장 정보를 확인
        UserChatEntity userChat = userChatRepository.findAllByChatRoomChatroomId(chatRoom.getChatroomId())
                .stream()
                .filter(chat -> chat.getMember().getMemberId().equals(member.getMemberId()))
                .findFirst()
                .orElse(null);

        // 검증: 퇴장 정보가 올바르게 저장되었는지 확인
        assertNotNull(userChat); // 입장 정보가 존재해야 함
        assertNotNull(userChat.getLeftedAt()); // 퇴장 시간이 기록되어야 함
        assertTrue(userChat.getLeftedAt().isAfter(userChat.getJoinedAt())); // 퇴장 시간이 입장 시간 이후여야 함
    }

    @Test
    void testSaveMessage() {
        // Given: 테스트 데이터를 준비
        Integer chatroomId = 1;
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatroomId).orElseThrow();
        Member member = memberRepository.save(
                Member.builder()
                        .nickname("testUser")
                        .email("test@example.com")
                        .oauthId("oauthTestId")
                        .oauthProvider("google")
                        .build()
        );
        String content = "Hello, World!";

        // When: 사용자가 메시지를 전송
        ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                .chatroomId(chatRoom.getChatroomId())
                .memberId(String.valueOf(member.getMemberId()))
                .content(content)
                .build();

        chatService.saveMessage(messageDTO);

        // Then: 데이터베이스에서 메시지 정보를 확인
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoomChatroomId(chatRoom.getChatroomId());
        assertFalse(messages.isEmpty()); // 메시지가 존재해야 함
        ChatMessageEntity message = messages.get(0); // 첫 번째 메시지 가져오기

        // 검증: 메시지 데이터가 올바르게 저장되었는지 확인
        assertEquals(chatRoom.getChatroomId(), message.getChatRoom().getChatroomId()); // 채팅방 ID가 일치해야 함
        assertEquals(member.getMemberId(), message.getMember().getMemberId()); // 사용자 ID가 일치해야 함
        assertEquals(content, message.getContent()); // 메시지 내용이 일치해야 함
    }
}
