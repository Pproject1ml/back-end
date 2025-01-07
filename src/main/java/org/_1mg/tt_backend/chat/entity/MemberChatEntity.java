package org._1mg.tt_backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.entity.Member;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_chat")
public class MemberChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberChatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // member 테이블과 연관

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoomEntity chatroom;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    public static MemberChatEntity create(ChatRoomEntity chatRoom, Member member) {

        return MemberChatEntity.builder()
                .member(member)
                .chatroom(chatRoom)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public void leave() {
        this.leftAt = LocalDateTime.now();
    }
}
