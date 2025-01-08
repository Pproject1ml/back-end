package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.chat.dto.MemberChatDTO;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_chat")
public class MemberChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberChatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile; // profile 테이블과 연관

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatroomEntity chatroom;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    public static MemberChatEntity create(ChatroomEntity chatRoom, Profile profile) {

        return MemberChatEntity.builder()
                .profile(profile)
                .chatroom(chatRoom)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public void leave() {
        this.leftAt = LocalDateTime.now();
    }

    public MemberChatDTO convertToDTO() {

        return MemberChatDTO.builder()
                .profile(profile.convertToDTO())
                .chatRoom(chatroom.convertToDTO())
                .joinedAt(joinedAt)
                .leftAt(leftAt)
                .build();
    }
}
