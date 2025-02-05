package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.dto.PrivateChatroomDTO;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "private_chatroom")
public class PrivateChatroomEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privateChatroomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    private Profile user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    private Profile user2;

    private boolean user1Alarm;
    private boolean user2Alarm;

    private boolean user1Presence;
    private boolean user2Presence;

    private boolean active;

    public static PrivateChatroomEntity create(Profile user1, Profile user2) {

        return PrivateChatroomEntity.builder()
                .user1(user1)
                .user2(user2)
                .user1Alarm(true)
                .user2Alarm(true)
                .user1Presence(false)
                .user2Presence(false)
                .active(true)
                .build();
    }

    public PrivateChatroomDTO convertToDTO() {

        return PrivateChatroomDTO.builder()
                .chatroomId(this.privateChatroomId.toString())
                .alarm(true)
                .active(this.active)
                .build();
    }

    public void restore() {
        this.active = false;
    }

    public void detachProfile(Long profileId) {

        if (user1 != null && user1.getProfileId().equals(profileId)) user1 = null;
        if (user2 != null && user2.getProfileId().equals(profileId)) user2 = null;
    }

    public void changeUser1Alarm(Boolean alarm) {
        this.user1Alarm = alarm;
    }

    public void changeUser2Alarm(Boolean alarm) {
        this.user2Alarm = alarm;
    }

    public void leaveUser1() {
        this.user1Presence = false;
    }

    public void leaveUser2() {
        this.user1Presence = true;
    }

    public void enterUser1() {
        this.user1Presence = true;
    }

    public void enterUser2() {
        this.user2Presence = true;
    }
}
