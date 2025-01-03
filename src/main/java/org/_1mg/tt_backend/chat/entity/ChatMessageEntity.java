package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Member;

import java.time.LocalDateTime;

// 채팅 메시지 엔터티
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_message")
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoomEntity chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // member 테이블과 연관

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_read")
    private Boolean isRead = false;

    // 매개변수 6개를 받는 생성자 추가
    public ChatMessageEntity(ChatRoomEntity chatRoom, Member member, String content, LocalDateTime createdAt, Boolean isRead) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.content = content;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public static ChatMessageEntity create(ChatRoomEntity chatRoom, Member member, String content) {
        return new ChatMessageEntity(chatRoom, member, content, LocalDateTime.now(), false);
    }
}
