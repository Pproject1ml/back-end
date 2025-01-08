package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileChatRepository extends JpaRepository<ProfileChatroomEntity, Long> {

}