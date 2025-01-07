package org._1mg.tt_backend.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org._1mg.tt_backend.auth.entity.Member;
import org._1mg.tt_backend.auth.jwt.JwtUtils;
import org._1mg.tt_backend.auth.repository.MemberRepository;
import org._1mg.tt_backend.exception.auth.CustomJwtException;
import org._1mg.tt_backend.exception.auth.JwtExpiredTokenException;
import org._1mg.tt_backend.exception.member.NicknameAlreadyExistsException;
import org._1mg.tt_backend.exception.member.UserAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    public Member findMember(String memberId) {

        return memberRepository.findById(UUID.fromString(memberId)).orElseThrow();
    }

    public void updateMember(MemberDTO memberDTO, String memberId) {

        Member member = findMember(memberId);
        member.updateMember(memberDTO.checkNull(memberDTO, member));
    }

    public String checkUniqueNickname(String nickname) throws JsonProcessingException {

        Member member = memberRepository.findByNickname(nickname);
        if (member != null) {
            log.error("NOT UNIQUE NICKNAME");
            throw new NicknameAlreadyExistsException("NOT UNIQUE NICKNAME", nickname);
        }

        return objectMapper.writeValueAsString(Map.of("nickname", nickname));
    }


    public void signup(MemberDTO memberDTO) {

        Member beforeJoin = memberRepository.findByOauthId(memberDTO.getOauthId());

        if (beforeJoin != null) {
            log.error("USER ALREADY EXISTS");
            if (!beforeJoin.getIsDeleted()) {
                throw new UserAlreadyExistsException("USER ALREADY EXISTS");
            } else {
                beforeJoin.updateMember(memberDTO);
                return;
            }
        }

        memberRepository.save(Member.builder()
                .nickname(memberDTO.getNickname())
                .email(memberDTO.getEmail())
                .profileImage(memberDTO.getProfileImage())
                .introduction(memberDTO.getIntroduction())
                .age(memberDTO.getAge())
                .gender(memberDTO.getGender())
                .oauthId(memberDTO.getOauthId())
                .oauthProvider(memberDTO.getOauthProvider())
                .build());
    }

    public void saveRefreshToken(String memberId, String refreshToken) {

        Member member = memberRepository.findById(UUID.fromString(memberId)).orElseThrow();
        member.updateRefreshToken(refreshToken);
    }

    public String refresh(String memberId) {

        Member member = memberRepository.findById(UUID.fromString(memberId)).orElseThrow();

        if (member.getRefreshToken() == null) {
            log.error("REFRESH TOKEN IS NULL");
            throw new CustomJwtException("REFRESH TOKEN IS NULL");
        }

        try {
            jwtUtils.verifyToken(member.getRefreshToken());
        } catch (ExpiredJwtException e) {
            log.error("REFRESH TOKEN EXPIRED");
            throw new JwtExpiredTokenException("REFRESH TOKEN EXPIRED");
        } catch (Exception e) {
            log.error("REFRESH TOKEN ERROR");
            throw new CustomJwtException("REFRESH TOKEN ERROR");
        }

        Long EXPIRED_REFRESH = 12 * 30 * 24 * 60 * 60L;
        String refresh = jwtUtils.createJwt("refresh", memberId, member.getRole().toString(), EXPIRED_REFRESH);
        saveRefreshToken(memberId, refresh);

        Long EXPIRED_ACCESS = 3 * 30 * 24 * 60 * 60L;
        return jwtUtils.createJwt("access", member.getMemberId().toString(), member.getRole().toString(), EXPIRED_ACCESS);
    }

    public void logout(String memberId) {

        Member member = findMember(memberId);
        member.deleteRefreshToken();
    }
}