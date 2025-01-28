package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.MessageType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "private_message")
public class PrivateMessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privateMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "private_chatroom_id", nullable = false)
    private PrivateChatroomEntity chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile; // profile 테이블과 연관

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(columnDefinition = "TEXT")
    private String content;
}
