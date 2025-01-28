package org._1mg.tt_backend.auth.repository;


import org._1mg.tt_backend.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Member findByOauthId(String oauthId);

    @Query("SELECT m FROM Member m " +
            "WHERE m.isDeleted = false " +
            "AND m.oauthId = :oauthId ")
    Optional<Member> findByOauthIdNotDeleted(String oauthId);

    @Query("SELECT m FROM Member m " +
            "WHERE m.isDeleted = false " +
            "AND m.memberId = :memberId")
    Optional<Member> findByIdNotDeleted(@Param("memberId") UUID memberId);

    @Query("SELECT m FROM Member m " +
            "JOIN FETCH m.profile p " +
            "WHERE m.isDeleted = false " +
            "AND m.memberId = :memberId")
    Optional<Member> findMemberAndProfileNotDeleted(@Param("memberId") UUID memberId);
}
