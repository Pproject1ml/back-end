package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.MemberChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatRepository extends JpaRepository<MemberChatEntity, Long> {

    // 채팅방 ID(chatroomId)를 기준으로 모든 UserChatEntity를 가져옵니다.
    //List<MemberChatEntity> findAllByChatroomChatroomId(Long chatroomId);

    // 가장 최근에 삽입된 UserChatEntity를 기준으로 lefted_at을 업데이트
    //MemberChatEntity findFirstByChatroomChatroomIdAndMemberMemberIdOrderByJoinedAtDesc(Long chatroomId, UUID memberId);
}