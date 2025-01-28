package org._1mg.tt_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.dto.PrivateChatDTO;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org._1mg.tt_backend.chat.service.PrivateChatroomService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org._1mg.tt_backend.base.CustomException.OK;

@RequestMapping("/chat")
@Controller
@RequiredArgsConstructor
public class PrivateChatroomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final PrivateChatroomService privateChatroomService;
    private final ProfileService profileService;

    private final String INVITE_DESTINATION = "/sub/chat-invite";

    @PostMapping("/private")
    public ResponseDTO<PrivateChatDTO> makePrivateChatroom(@RequestBody PrivateChatDTO privateChat, Principal principal) {

        Profile user2 = profileService.findProfileAndMember(privateChat.getProfileId());
        String destination = privateChatroomService.createPrivateChatroom(principal.getName(), user2);
        privateChat.setDestination(destination);


        messagingTemplate.convertAndSendToUser(user2.getMember().getMemberId(), INVITE_DESTINATION, );
        return ResponseDTO.<PrivateChatDTO>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(privateChat)
                .build();
    }
}
