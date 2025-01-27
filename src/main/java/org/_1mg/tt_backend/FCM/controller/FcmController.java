package org._1mg.tt_backend.FCM.controller;

import org._1mg.tt_backend.FCM.service.FcmService;
import org._1mg.tt_backend.auth.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/fcm")
public class FcmController {

    private final FcmService fcmService;
    private final ProfileService profileService; // ProfileService 추가

    public FcmController(FcmService fcmService, ProfileService profileService) {
        this.fcmService = fcmService;
        this.profileService = profileService;
    }

    // 단톡방 알림 전송
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, Object> requestBody) {
        Long chatroomId = Long.valueOf(requestBody.get("chatroomId").toString());
        Long profileId = Long.valueOf(requestBody.get("profileId").toString());
        String content = requestBody.get("content").toString();

        String senderName = profileService.findById(profileId).getNickname();

        // 알림 전송
        fcmService.sendNotificationToChatroom(chatroomId, senderName, content);
        return ResponseEntity.ok("Notification sent");
    }


    //FCM 토큰 저장/갱신 API
    @PostMapping("/register-token")
    public ResponseEntity<String> registerFcmToken(@RequestBody Map<String, Object> requestBody) {
        Long profileId = Long.valueOf(requestBody.get("profileId").toString());
        String fcmToken = requestBody.get("fcmToken").toString();
        fcmService.registerFcmToken(profileId, fcmToken);
        return ResponseEntity.ok("FCM Token 전달 완료");
    }
}