package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;

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

    @OneToMany(mappedBy = "chatroom")
    private List<MessageEntity> messageEntities;

    @OneToMany(mappedBy = "chatroom")
    private List<ProfileChatroomEntity> profileChatrooms;

    public ChatroomDTO convertToDTOWithChatroomInfo() {

        return ChatroomDTO.builder()
                .chatroomId(this.chatroomId)
                .title(this.title)
                .createdAt(super.getCreatedAt())
                .updatedAt(super.getUpdatedAt())
                .build();
    }
}
