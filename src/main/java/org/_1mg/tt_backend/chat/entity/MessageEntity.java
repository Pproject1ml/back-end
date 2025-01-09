package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.MessageDTO;

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
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile; // profile 테이블과 연관

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    public static MessageEntity create(ChatroomEntity chatroomEntity, Profile profile, String content) {

        return MessageEntity.builder()
                .chatroom(chatroomEntity)
                .profile(profile)
                .content(content)
                .build();
    }

    public MessageDTO convertToDTO() {

        return MessageDTO.builder()
                .messageId(this.messageId)
                .profile(this.profile.convertToDTO())
                .content(this.content)
                .messageType(this.messageType)
                .createdAt(super.getCreatedAt())
                .build();

    }

}
