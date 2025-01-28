package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.PrivateMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessageEntity, Long> {
}
