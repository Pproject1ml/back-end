package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Member;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.MessageType;

// 채팅 메시지 엔터티
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_message")
@Builder
public class ChatMessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoomEntity chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // member 테이블과 연관

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    public static ChatMessageEntity create(ChatRoomEntity chatRoom, Member member, String content) {

        return ChatMessageEntity.builder()
                .chatroom(chatRoom)
                .member(member)
                .content(content)
                .build();
    }
}
