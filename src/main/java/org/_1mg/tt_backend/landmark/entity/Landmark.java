package org._1mg.tt_backend.landmark.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Landmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long landmarkId;
    private String name;
    private Double latitude;
    private Double longitude;
    @Builder.Default
    private Integer radius = 5000;
    private String imagePath;

    @OneToOne
    @JoinColumn(name = "chatroom_id")
    private ChatroomEntity chatroom;

    public LandmarkDTO convertToDTO() {

        return LandmarkDTO.builder()
                .name(this.getName())
                .latitude(this.getLatitude())
                .longitude(this.getLongitude())
                .radius(this.getRadius())
                .imagePath(this.getImagePath())
                .chatroom(this.chatroom.convertToDTOWithChatroomInfo())
                .build();
    }
}
