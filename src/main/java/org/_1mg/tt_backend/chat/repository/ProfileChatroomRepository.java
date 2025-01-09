package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileChatroomRepository extends JpaRepository<ProfileChatroomEntity, Long> {

    boolean existsByProfile_ProfileIdAndChatroom_ChatroomId(Long profileId, Long chatroomId);
}