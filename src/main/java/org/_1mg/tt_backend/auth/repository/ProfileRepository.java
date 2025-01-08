package org._1mg.tt_backend.auth.repository;

import org._1mg.tt_backend.auth.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    public Profile findByNickname(String username);

//    @Query("SELECT m FROM Member m JOIN FETCH m.chatrooms WHERE m.id = :memberId")
//    Member findChatroomsByMemberId(UUID memberId);
}
