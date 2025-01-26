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

    public ChatroomDTO convertToDTOForMap(double longitude, double latitude, String imagePath) {

        return ChatroomDTO.builder()
                .chatroomId(this.chatroomId.toString())
                .title(this.title)
                .count(this.count)
                .longitude(longitude)
                .latitude(latitude)
                .imagePath(imagePath)
                .createdAt(super.getCreatedAt())
                .updatedAt(super.getUpdatedAt())
                .build();
    }

    public ChatroomDTO convertToDTOForTab(boolean active, boolean alarm) {

        return ChatroomDTO.builder()
                .chatroomId(this.chatroomId.toString())
                .title(this.title)
                .count(this.count)
                .alarm(alarm)
                .longitude(this.landmark.getLongitude())
                .latitude(this.landmark.getLatitude())
                .imagePath(this.landmark.getImagePath())
                .createdAt(super.getCreatedAt())
                .updatedAt(super.getUpdatedAt())
                .active(active)
                .build();
    }

    public static ChatroomEntity create(String title) {

        return ChatroomEntity.builder()
                .title(title)
                .build();
    }

    public void delete() {
        super.updateDeleted(true);
    }

    public void restore() {
        super.updateDeleted(false);
    }

    public void join() {
        this.count++;
    }

    public void die() {
        this.count--;
    }
}
