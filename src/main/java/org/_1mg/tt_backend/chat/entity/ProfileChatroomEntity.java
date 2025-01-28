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
    @JoinColumn(name = "profile_id")
    private Profile profile; // profile 테이블과 연관

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatroomEntity chatroom;

    private boolean alarm;

    private boolean active;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    private boolean isDeleted;

    public static ProfileChatroomEntity create(Profile profile, ChatroomEntity chatroomEntity) {

        chatroomEntity.join();
        return ProfileChatroomEntity.builder()
                .profile(profile)
                .chatroom(chatroomEntity)
                .active(true)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public void leave() {
        this.leftAt = LocalDateTime.now();
    }

    public void disable() {
        this.active = false;
        this.chatroom.die();
    }

    public void enable() {
        this.chatroom.join();
        this.active = true;
    }

    public void die() {
        this.isDeleted = true;
        this.chatroom.die();
    }

    public void restore() {
        this.chatroom.join();
        this.isDeleted = false;
    }

    public void changeAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public void detachProfile() {
        this.profile = null;
        this.isDeleted = true;
    }
}
