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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chatroom_id", nullable = true) // 단방향 관계
    private ChatroomEntity chatroom;

    /**
     * 랜드마크와 채팅방을 연관짓는 메서드
     *
     * @param chatroom 연결할 채팅방 엔터티
     */
    public void assignChatroom(ChatroomEntity chatroom) {
        if (this.chatroom != null) {
            throw new IllegalStateException("Chatroom is already assigned to this landmark.");
        }
        this.chatroom = chatroom;
    }

    public LandmarkDTO convertToDTO() {

        return LandmarkDTO.builder()
                .name(this.getName())
                .latitude(this.getLatitude())
                .longitude(this.getLongitude())
                .radius(this.getRadius())
                .imagePath(this.getImagePath())
                .chatroom(this.chatroom.convertToDTOWithChatroomInfo()) // Chatroom 정보 포함
                .build();
    }
}
