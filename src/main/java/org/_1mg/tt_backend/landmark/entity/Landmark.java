package org._1mg.tt_backend.landmark.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Landmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    @Builder.Default
    private Integer radius = 5000;
    private String imagePath;
    //ChatRoom과 함께 바꿔야 함 일단은 연관 관계 매핑 안함
    //    @OneToOne
//    @JoinColumn(name = "chatRoom_id")
//    private ChatRoomEntity chatRoom;
    private Integer chatRoomId;

    public LandmarkDTO convertToDTO() {

        return LandmarkDTO.builder()
                .name(this.getName())
                .latitude(this.getLatitude())
                .longitude(this.getLongitude())
                .radius(this.getRadius())
                .imagePath(this.getImagePath())
                .chatRoomId(this.getChatRoomId())
                //.chatRoomId(this.getChatRoom().getChatroomId())
                .build();

    }
}
