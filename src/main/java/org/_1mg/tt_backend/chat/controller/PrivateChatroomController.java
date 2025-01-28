package org._1mg.tt_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.PrivateChatDTO;
import org._1mg.tt_backend.chat.service.PrivateChatroomService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static org._1mg.tt_backend.base.CustomException.OK;

@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
public class PrivateChatroomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final PrivateChatroomService chatroomService;
    private final ProfileService profileService;

    final String INVITE_DESTINATION = "/sub/chat-invite";

    @PostMapping("/private-chat")
    public ResponseDTO<PrivateChatDTO> makePrivateChatroom(@RequestBody PrivateChatDTO privateChat, Principal principal) {

        Profile user2 = profileService.findProfileAndMember(privateChat.getProfileId());
        String destination = chatroomService.createPrivateChatroom(principal.getName(), user2);
        privateChat.setDestination(destination);
        privateChat.setProfileId(null);

        messagingTemplate.convertAndSendToUser(user2.getMember().getMemberId().toString(), INVITE_DESTINATION, privateChat);
        return ResponseDTO.<PrivateChatDTO>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(privateChat)
                .build();
    }

//    @GetMapping("/list")
//    public ResponseDTO<List<ChatroomDTO>> chatList(@RequestParam("id") Long profileId) {
//
//        List<ChatroomDTO> result = chatroomService.getChatrooms(profileId);
//
//        return ResponseDTO.<List<ChatroomDTO>>builder()
//                .status(OK.getStatus())
//                .message(OK.getMessage())
//                .data(result)
//                .build();
//    }
//
//    @PostMapping("/alarm")
//    public ResponseDTO<String> setAlarm(@RequestBody AlarmDTO alarmDTO) {
//
//        chatroomService.changeAlarm(alarmDTO);
//
//        return ResponseDTO.<String>builder()
//                .status(OK.getStatus())
//                .message(OK.getMessage())
//                .build();
//    }
}
