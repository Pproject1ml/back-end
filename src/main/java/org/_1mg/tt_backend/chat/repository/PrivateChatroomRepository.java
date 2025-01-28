package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.PrivateChatroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateChatroomRepository extends JpaRepository<PrivateChatroomEntity, Long> {
}
