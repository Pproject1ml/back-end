package org._1mg.tt_backend.FCM.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final ProfileService profileService;
    private final FcmSender fcmSender;

    public void sendNotificationToChatroom(Long chatroomId, String senderName, String content) {
        //채팅방 참여 중인 인원 프로필 조회
        List<ProfileDTO> profiles = profileService.findProfiles(chatroomId); //채팅방 인원 프로필 조회


        List<String> tokens = profiles.stream() //참여자 토큰 list 추출
                .map(ProfileDTO::getFcmToken)
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());

        if (!tokens.isEmpty()) {
            String title = senderName;
            fcmSender.sendNotification(tokens, title, content); //토큰list로 알림 전송
        }
    }

    public void registerFcmToken(Long profileId, String fcmToken) {
        Profile profile = profileService.findById(profileId);
        profile.updateFcmToken(fcmToken); //토큰 업데이트
        profileService.save(profile);
    }
}
