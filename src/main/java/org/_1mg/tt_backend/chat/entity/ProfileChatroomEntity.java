package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profile_chatroom")
public class ProfileChatroomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileChatroomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile; // profile 테이블과 연관

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatroomEntity chatroom;

    private boolean alarm;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    private boolean isDeleted;

    public static ProfileChatroomEntity create(Profile profile, ChatroomEntity chatroomEntity) {

        return ProfileChatroomEntity.builder()
                .profile(profile)
                .chatroom(chatroomEntity)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public void leave() {
        this.leftAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void changeAlarm(boolean alarm) {
        this.alarm = alarm;
    }
}
