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
class ChatServiceIntegrationTest {

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
    void testMultipleUsersJoinAndLeaveChatRoom() {
        // Given: 채팅방과 여러 사용자를 생성
        ChatRoomEntity chatRoom = chatRoomRepository.save(new ChatRoomEntity(123));
        Member[] members = {
                memberRepository.save(Member.builder().nickname("user1").email("user1@example.com").oauthId("oauth1").oauthProvider("google").build()),
                memberRepository.save(Member.builder().nickname("user2").email("user2@example.com").oauthId("oauth2").oauthProvider("google").build()),
                memberRepository.save(Member.builder().nickname("user3").email("user3@example.com").oauthId("oauth3").oauthProvider("google").build())
        };

        // When: 각 사용자가 채팅방에 입장
        for (Member member : members) {
            chatService.userJoinChatRoom(chatRoom.getChatroomId(), member.getMemberId());
        }

        // Then: 각 사용자의 입장 정보가 저장되었는지 확인
        for (Member member : members) {
            UserChatEntity userChat = userChatRepository.findAllByChatRoomChatroomId(chatRoom.getChatroomId())
                    .stream()
                    .filter(chat -> chat.getMember().getMemberId().equals(member.getMemberId()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(userChat);
            assertNotNull(userChat.getJoinedAt());
            assertNull(userChat.getLeftedAt());
        }

        // 사용자 퇴장
        for (Member member : members) {
            chatService.userLeaveChatRoom(chatRoom.getChatroomId(), member.getMemberId());
        }

        // 퇴장 후 각 사용자의 퇴장 정보가 업데이트되었는지 확인
        for (Member member : members) {
            UserChatEntity userChat = userChatRepository.findAllByChatRoomChatroomId(chatRoom.getChatroomId())
                    .stream()
                    .filter(chat -> chat.getMember().getMemberId().equals(member.getMemberId()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(userChat);
            assertNotNull(userChat.getLeftedAt());
            assertTrue(userChat.getLeftedAt().isAfter(userChat.getJoinedAt()));
        }
    }

    @Test
    void testMultipleUsersSendMessages() {
        // Given: 채팅방과 여러 사용자를 생성
        ChatRoomEntity chatRoom = chatRoomRepository.save(new ChatRoomEntity(123));
        Member[] members = {
                memberRepository.save(Member.builder().nickname("user1").email("user1@example.com").oauthId("oauth1").oauthProvider("google").build()),
                memberRepository.save(Member.builder().nickname("user2").email("user2@example.com").oauthId("oauth2").oauthProvider("google").build()),
                memberRepository.save(Member.builder().nickname("user3").email("user3@example.com").oauthId("oauth3").oauthProvider("google").build())
        };
        String[] messages = {"Hello from user1", "Hi from user2", "How are you from user3"};

        // When: 각 사용자가 메시지를 보냄
        for (int i = 0; i < members.length; i++) {
            ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                    .chatroomId(chatRoom.getChatroomId())
                    .memberId(members[i].getMemberId().toString())
                    .content(messages[i])
                    .build();
            chatService.saveMessage(messageDTO); // saveMessage 호출 추가
        }

        // Then: 각 메시지가 저장되었는지 확인
        List<ChatMessageEntity> savedMessages = chatMessageRepository.findByChatRoomChatroomId(chatRoom.getChatroomId());
        assertEquals(3, savedMessages.size());

        for (int i = 0; i < savedMessages.size(); i++) {
            ChatMessageEntity message = savedMessages.get(i);
            assertEquals(chatRoom.getChatroomId(), message.getChatRoom().getChatroomId());
            assertEquals(members[i].getMemberId(), message.getMember().getMemberId());
            assertEquals(messages[i], message.getContent());
        }
    }
}

