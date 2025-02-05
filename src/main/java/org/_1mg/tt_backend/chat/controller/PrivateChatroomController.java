package org._1mg.tt_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.AlarmDTO;
import org._1mg.tt_backend.chat.dto.PrivateChatDTO;
import org._1mg.tt_backend.chat.dto.PrivateChatroomDTO;
import org._1mg.tt_backend.chat.service.PrivateChatroomService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static org._1mg.tt_backend.base.CustomException.OK;

@Slf4j
@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
public class PrivateChatroomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final PrivateChatroomService chatroomService;
    private final ProfileService profileService;

    final String INVITE_DESTINATION = "/sub/chat-invite";

    @PostMapping("/private-chat")
    public ResponseDTO<PrivateChatroomDTO> makePrivateChatroom(@RequestBody PrivateChatDTO privateChat, Principal principal) {

        Profile user2 = profileService.findProfileAndMember(privateChat.getProfileId());
        PrivateChatroomDTO chatroom = chatroomService.createPrivateChatroom(principal.getName(), user2);

        messagingTemplate.convertAndSendToUser(user2.getMember().getMemberId().toString(), INVITE_DESTINATION, chatroom);
        return ResponseDTO.<PrivateChatroomDTO>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(chatroom)
                .build();
    }

    @GetMapping("/private-list")
    public ResponseDTO<List<PrivateChatroomDTO>> chatList(@RequestParam("id") Long profileId) {

        log.info("START PRIVATE CHATROOM LIST");
        List<PrivateChatroomDTO> result = chatroomService.getChatrooms(profileId);
        log.info(result.toString());

        log.info("END PRIVATE CHATROOM LIST");
        return ResponseDTO.<List<PrivateChatroomDTO>>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(result)
                .build();
    }

    @PostMapping("/private-alarm")
    public ResponseDTO<String> setAlarm(@RequestBody AlarmDTO alarmDTO) {

        chatroomService.changeAlarm(alarmDTO);

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }
}
