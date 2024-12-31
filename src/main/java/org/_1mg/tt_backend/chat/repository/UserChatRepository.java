package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.UserChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserChatRepository extends JpaRepository<UserChatEntity, Integer> {
    // 채팅방 ID(chatroomId)를 기준으로 모든 UserChatEntity를 가져옵니다.
    List<UserChatEntity> findByChatRoomChatroomId(Integer chatroomId);

    // 가장 최근에 삽입된 UserChatEntity를 기준으로 lefted_at을 업데이트
    UserChatEntity findFirstByChatRoomChatroomIdAndMember_MemberIdOrderByJoinedAtDesc(Integer chatroomId, UUID memberId);

}