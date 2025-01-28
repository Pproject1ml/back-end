package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.TextDTO;

// 채팅 메시지 엔터티
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "message")
public class MessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatroomEntity chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = true)
    private Profile profile; // profile 테이블과 연관

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(columnDefinition = "TEXT")
    private String content;

    public static MessageEntity create(ChatroomEntity chatroomEntity, Profile profile, MessageType messageType, String content) {

        return MessageEntity.builder()
                .chatroom(chatroomEntity)
                .profile(profile)
                .messageType(messageType)
                .content(content)
                .build();
    }

    public TextDTO convertToText() {

        return TextDTO.builder()
                .messageId(this.messageId.toString())
                .profileId(this.profile.getProfileId().toString())
                .chatroomId(this.chatroom.getChatroomId().toString())
                .content(this.content)
                .messageType(this.messageType)
                .createdAt(super.getCreatedAt())
                .build();

    }

    public void detachProfile() {
        this.profile = null;
    }
}
