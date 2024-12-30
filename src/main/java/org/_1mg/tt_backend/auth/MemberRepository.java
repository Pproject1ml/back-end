package org._1mg.tt_backend.auth;


import org._1mg.tt_backend.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Member findByOauthId(String oauthId);

    Member findByNickname(String nickname);
}
