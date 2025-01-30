package org._1mg.tt_backend.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.FCM.exception.InvalidFCMToken;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Member;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.exception.member.custom.UserAlreadyExistsException;
import org._1mg.tt_backend.auth.jwt.JwtUtils;
import org._1mg.tt_backend.auth.repository.MemberRepository;
import org._1mg.tt_backend.chat.service.ChatUtils;
import org._1mg.tt_backend.chat.service.MessageService;
import org._1mg.tt_backend.chat.service.PrivateMessageService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static org._1mg.tt_backend.base.CustomException.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final ProfileService profileService;
    private final MessageService messageService;
    private final PrivateMessageService privateMessageService;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final ChatUtils chatUtils;

    public Member findMemberNotDeleted(String memberId) {

        return memberRepository.findByIdNotDeleted(UUID.fromString(memberId)).orElseThrow(() ->
                new UsernameNotFoundException(USER_NOT_FOUND.getMessage()));
    }

    public void updateMember(ProfileDTO profileDTO, String memberId) {

        Member member = findMemberNotDeleted(memberId);
        member.updateProfile(profileDTO);
    }

    public String checkUniqueNickname(String nickname) throws JsonProcessingException {

        profileService.checkUniqueNickname(nickname);
        return objectMapper.writeValueAsString(Map.of("nickname", nickname));
    }


    public void signup(MemberDTO memberDTO) {

        Member beforeJoin = memberRepository.findByOauthId(memberDTO.getOauthId());

        if (beforeJoin != null) {
            if (!beforeJoin.isDeleted()) {
                throw new UserAlreadyExistsException(ALREADY_EXISTS_USER.getMessage());
            }

            beforeJoin.updateMember(memberDTO);
            return;
        }

        memberRepository.save(Member.createMember(memberDTO));
    }

    public void saveRefreshToken(String memberId, String refreshToken) {

        Member member = findMemberNotDeleted(memberId);
        member.updateRefreshToken(refreshToken);
    }

    public String refresh(String memberId) {

        Member member = findMemberNotDeleted(memberId);

        if (member.getRefreshToken() == null) {
            throw new IllegalArgumentException(REFRESH_TOKEN_IS_NULL.getMessage());
        }

        jwtUtils.verifyToken(member.getRefreshToken());

        Long EXPIRED_REFRESH = 12 * 30 * 24 * 60 * 60L;
        String refresh = jwtUtils.createJwt("refresh", memberId, member.getRole().toString(), EXPIRED_REFRESH);
        member.updateRefreshToken(refresh);

        Long EXPIRED_ACCESS = 3 * 30 * 24 * 60 * 60L;
        return jwtUtils.createJwt("access", member.getMemberId().toString(), member.getRole().toString(), EXPIRED_ACCESS);
    }

    public void logout(String memberId) {

        Member member = findMemberNotDeleted(memberId);
        member.deleteRefreshToken();
    }

    public void deleteMember(String memberId) {

        Member member = memberRepository.findMemberAndProfileNotDeleted(UUID.fromString(memberId)).orElseThrow(
                () -> new UsernameNotFoundException(USER_NOT_FOUND.getMessage())
        );

        messageService.toNullSender(member.getProfile().getProfileId());
        privateMessageService.toNullSender(member.getProfile().getProfileId());
        chatUtils.toNullSender(member.getProfile().getProfileId());

        memberRepository.delete(member);

    }

    public void checkJwtToken(String token) {

        Claims claims = jwtUtils.verifyToken(token);
        String memberId = jwtUtils.getSubject(claims);
        findMemberNotDeleted(memberId);
    }

    public void checkFcmToken(String profileId, String token) {

        Profile profile = profileService.findProfile(profileId);

        if (!profile.getFcmToken().equals(token)) {
            throw new InvalidFCMToken(INVALID_FCM_TOKEN.getMessage());
        }
    }
}
