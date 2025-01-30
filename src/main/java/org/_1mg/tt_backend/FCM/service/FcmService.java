package org._1mg.tt_backend.FCM.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final ProfileService profileService;
    private final FcmSender fcmSender;

    public void sendNotificationToChatroom(String chatroomId, String profileId, String content) {

        //채팅방 참여 중인 인원 프로필 조회
        List<ProfileDTO> profiles = profileService.findProfiles(Long.parseLong(chatroomId)); //채팅방 인원 프로필 조회

        List<String> tokens = new ArrayList<>();
        String senderName = null;
        for (ProfileDTO profile : profiles) {

            //본인이 보낸 메세지에 대해선 알림이 오면 안 됨
            if (profile.getProfileId().equals(profileId)) {
                senderName = profile.getNickname();
                continue;
            }

            tokens.add(profile.getFcmToken());
        }

        //메세지 보낸 후 나가기 한 경우 알림을 보내지 않음(보낸 이가 NULL인 경우 방지)
        if (!tokens.isEmpty() && senderName != null) {
            fcmSender.sendNotification(tokens, senderName, content);
        }
    }

    public void registerFcmToken(Long profileId, String fcmToken) {
        Profile profile = profileService.findById(profileId);
        profile.updateFcmToken(fcmToken); //토큰 업데이트
        profileService.save(profile);
    }
}
