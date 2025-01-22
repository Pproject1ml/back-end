package org._1mg.tt_backend.chat.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocketService {

    private final ProfileService profileService;
    private final MessageService messageService;

    public List<TextDTO> makeWelcomeMessage(String profileId, String chatroomId) {

        Profile profile = profileService.findProfile(profileId);
        String message = profile.getNickname() + "님이 입장하셨습니다";

        List<ProfileDTO> profiles = profileService.findProfiles(Long.parseLong(chatroomId));
        TextDTO text = TextDTO.builder()
                .chatroomId(chatroomId)
                .profileId(profileId)
                .messageType(MessageType.JOIN)
                .content(message)
                .profiles(profiles)
                .build();

        return messageService.sendText(text, Long.parseLong(chatroomId));
    }
}
