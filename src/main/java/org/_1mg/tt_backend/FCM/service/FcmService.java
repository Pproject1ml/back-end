package org._1mg.tt_backend.FCM.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.FCM.dto.FcmDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.chat.entity.PrivateChatroomEntity;
import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org._1mg.tt_backend.chat.service.ChatUtils;
import org._1mg.tt_backend.chat.service.PrivateChatroomService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FcmService {

    private final ProfileService profileService;
    private final ChatUtils chatUtils;
    private final PrivateChatroomService privateChatroomService;
    private final FcmSender fcmSender;

    public void sendNotificationToChatroom(String chatroomId, String profileId, String content) {

        //채팅방 참여 중인 인원 프로필 조회
        List<ProfileChatroomEntity> profiles = chatUtils.getProfilesForNotification(chatroomId); //채팅방 인원 프로필 조회

        List<String> tokens = new ArrayList<>();
        String senderName = null;
        for (ProfileChatroomEntity profileChatroom : profiles) {

            //본인이 보낸 메세지에 대해선 알림이 오면 안 됨
            if (profileChatroom.getProfile().getProfileId().equals(Long.parseLong(profileId))) {
                senderName = profileChatroom.getProfile().getNickname();
                continue;
            }

            if (!profileChatroom.isAlarm() || profileChatroom.isPresence()) {
                continue;
            }

            tokens.add(profileChatroom.getProfile().getFcmToken());
        }

        //메세지 보낸 후 나가기 한 경우 알림을 보내지 않음(보낸 이가 NULL인 경우 방지)
        if (!tokens.isEmpty() && senderName != null) {
            fcmSender.sendNotification(tokens, senderName, content);
        }
    }

    public void sendNotificationToPrivateChatroom(String chatroomId, String profileId, String content) {

        //채팅방 참여 중인 인원 프로필 조회
        PrivateChatroomEntity privateChatroom = privateChatroomService.getProfileForNotification(chatroomId); //채팅방 인원 프로필 조회

        String token = null;
        String senderName = null;

        //본인이 보낸 메세지에 대해선 알림이 오면 안 됨
        if (privateChatroom.getUser1().getProfileId().equals(Long.parseLong(profileId))) {
            senderName = privateChatroom.getUser1().getNickname();

            if (privateChatroom.isUser2Alarm() && !privateChatroom.isUser2Presence()) {
                token = privateChatroom.getUser2().getFcmToken();
            }
        } else {
            senderName = privateChatroom.getUser2().getNickname();

            if (privateChatroom.isUser1Alarm() && !privateChatroom.isUser1Presence()) {
                token = privateChatroom.getUser1().getFcmToken();
            }
        }

        //메세지 보낸 후 나가기 한 경우 알림을 보내지 않음(보낸 이가 NULL인 경우 방지)
        if (token != null && senderName != null) {
            fcmSender.sendPrivateNotification(token, senderName, content);
        }
    }

    public void registerFcmToken(String memberId, FcmDTO fcmDTO) {

        Profile profile = profileService.findProfileWithMemberId(memberId);
        //토큰 업데이트
        profile.updateFcmToken(fcmDTO.getToken());
    }
}
