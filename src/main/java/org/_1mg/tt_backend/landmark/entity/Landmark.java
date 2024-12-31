package org._1mg.tt_backend.landmark.entity;

import jakarta.persistence.*;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.entity.ChatRoomEntity;

@Entity
public class Landmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String latitude;
    private String longitude;
    private Integer radius;
    private String imagePath;

    @OneToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoomEntity chatRoom;
}
