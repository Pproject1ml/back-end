package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;
import org._1mg.tt_backend.landmark.entity.Landmark;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "chatroom")
public class ChatroomEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatroomId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int count;

    @OneToMany(mappedBy = "chatroom")
    private List<MessageEntity> messageEntities;

    @OneToMany(mappedBy = "chatroom")
    private List<ProfileChatroomEntity> profileChatrooms;

    @OneToOne(mappedBy = "chatroom")
    private Landmark landmark;

    public ChatroomDTO convertToDTOForMap(double longitude, double latitude) {

        return ChatroomDTO.builder()
                .chatroomId(this.chatroomId.toString())
                .title(this.title)
                .count(this.count)
                .longitude(longitude)
                .latitude(latitude)
                .createdAt(super.getCreatedAt())
                .updatedAt(super.getUpdatedAt())
                .build();
    }

    public ChatroomDTO convertToDTOForTab() {

        return ChatroomDTO.builder()
                .chatroomId(this.chatroomId.toString())
                .title(this.title)
                .count(this.count)
                .longitude(this.landmark.getLongitude())
                .latitude(this.landmark.getLatitude())
                .createdAt(super.getCreatedAt())
                .updatedAt(super.getUpdatedAt())
                .build();
    }

    public static ChatroomEntity create(String title) {

        return ChatroomEntity.builder()
                .title(title)
                .build();
    }

    public void deleteTrue() {
        super.updateDeleted(true);
    }

    public void deleteFalse() {
        super.updateDeleted(false);
    }

    public void updateTmp(int count) {
        this.count = count;
    }

    public int join() {
        return ++this.count;
    }

    public int die() {
        return --this.count;
    }
}
