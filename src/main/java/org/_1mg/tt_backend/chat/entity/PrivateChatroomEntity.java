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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    private Profile user1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    private Profile user2;

    private boolean alarm;

    private boolean active;

    public PrivateChatroomDTO convertToDTOForTab() {

        return PrivateChatroomDTO.builder()
                .privateChatroomId(this.privateChatroomId.toString())
                .alarm(this.alarm)
                .active(this.active)
                .build();
    }
}
