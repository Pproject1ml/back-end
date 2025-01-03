package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Member;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_chat")
public class UserChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_chat_id")
    private Integer userChatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // member 테이블과 연관

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoomEntity chatRoom;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "lefted_at")
    private LocalDateTime leftedAt;

    // 생성자 추가
    public UserChatEntity(ChatRoomEntity chatRoom, Member member, LocalDateTime joinedAt, LocalDateTime leftedAt) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.joinedAt = joinedAt;
        this.leftedAt = leftedAt;
    }

    public static UserChatEntity create(ChatRoomEntity chatRoom, Member member) {
        return new UserChatEntity(chatRoom, member, LocalDateTime.now(), null);
    }

    public void leave() {
        this.leftedAt = LocalDateTime.now();
    }
}
