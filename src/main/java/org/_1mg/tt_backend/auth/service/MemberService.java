package org._1mg.tt_backend.auth.service;

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
import org._1mg.tt_backend.chat.service.PrivateChatroomService;
import org._1mg.tt_backend.chat.service.PrivateMessageService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final PrivateChatroomService privateChatroomService;
    private final JwtUtils jwtUtils;
    private final ChatUtils chatUtils;

    private final S3Service s3Service;

    public Member findMemberNotDeleted(String memberId) {

        return memberRepository.findByIdNotDeleted(UUID.fromString(memberId)).orElseThrow(() ->
                new UsernameNotFoundException(USER_NOT_FOUND.getMessage()));
    }

    public Member findMemberAndProfile(String memberId) {

        return memberRepository.findMemberAndProfileNotDeleted(UUID.fromString(memberId)).orElseThrow(
                () -> new UsernameNotFoundException(USER_NOT_FOUND.getMessage())
        );
    }

    public ProfileDTO updateMember(ProfileDTO profileDTO, MultipartFile profileImage, String memberId) throws IOException {

        Member member = findMemberNotDeleted(memberId);
        Profile profile = member.getProfile();

        //이미지 처리: S3 업로드 또는 기본 이미지 사용
        String updatedImageUrl;
        if (profileImage != null && !profileImage.isEmpty()) {
            updatedImageUrl = s3Service.uploadProfileImage(profileImage, memberId);
        } else if (profile.getProfileImage().isEmpty()) {
            updatedImageUrl = s3Service.getDefaultProfileImage(); // 기존 이미지 없으면 기본 이미지 적용
        } else {
            updatedImageUrl = profile.getProfileImage(); // 기존 이미지 유지
        }

        //기존 값 유지하면서 업데이트 수행
        profileDTO.setProfileImage(updatedImageUrl); // 이미지 경로 업데이트
        member.updateProfile(profileDTO);

        return profileDTO;
    }

    public void checkUniqueNickname(String nickname) {

        profileService.checkUniqueNickname(nickname);
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

        Member member = findMemberAndProfile(memberId);

        log.info("MESSAGE SERVICE");
        messageService.toNullSender(member.getProfile().getProfileId());
        log.info("PRIVATE MESSAGE SERVICE");
        privateMessageService.toNullSender(member.getProfile().getProfileId());

        log.info("CHATROOM SERVICE");
        chatUtils.toNullSender(member.getProfile().getProfileId());

        log.info("PRIVATE CHATROOM SERVICE");
        privateChatroomService.toNullSender(member.getProfile().getProfileId());

        memberRepository.delete(member);

    }

    public MemberDTO checkJwtToken(String token) {

        Claims claims = jwtUtils.verifyToken(token);
        String memberId = jwtUtils.getSubject(claims);
        return findMemberAndProfile(memberId).convertToDTO();
    }

    public void checkFcmToken(String profileId, String token) {

        Profile profile = profileService.findProfile(profileId);

        if (profile.getFcmToken() == null) {
            throw new InvalidFCMToken(INVALID_MESSAGE_TYPE.getMessage());
        }

        if (!profile.getFcmToken().equals(token)) {
            throw new InvalidFCMToken(INVALID_FCM_TOKEN.getMessage());
        }
    }
}
