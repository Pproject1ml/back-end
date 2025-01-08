package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;

import java.util.List;
import java.util.stream.Collectors;

// 채팅방 엔터티
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
    private List<ChatMessageEntity> messages;

    @OneToMany(mappedBy = "chatroom")
    private List<MemberChatEntity> profiles;

    public ChatroomDTO convertToDTO() {

        return ChatroomDTO.builder()
                .chatroomId(this.chatroomId)
                .title(this.title)
                .messages(this.messages.stream()
                        .map(ChatMessageEntity::convertToDTO)
                        .collect(Collectors.toList()))
                .profiles(this.profiles.stream()
                        .map(MemberChatEntity::convertToDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
