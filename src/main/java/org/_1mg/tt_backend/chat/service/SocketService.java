package org._1mg.tt_backend.chat.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SocketService {

    private final ProfileService profileService;
    private final MessageService messageService;
    private final ChatroomService chatroomService;

    private final String WELCOME = "님이 입장하셨습니다";
    private final String BYE = "님이 퇴장하셨습니다";

    @Value("${chat.system}")
    private String SYSTEM_ID;
    private Profile SYSTEM;

    @PostConstruct
    public void initSystem() {
        SYSTEM = profileService.findProfile(SYSTEM_ID);
    }

    public List<TextDTO> preSendMessage(ChatroomEntity chatroom, MessageType messageType, String message) {

        //최신화된 참여자 정보 조회
        List<ProfileDTO> profiles = profileService.findProfiles(chatroom.getChatroomId());

        //퇴장 메세지는 SYSTEM이 보내야 함
        /*
            만약 profileID를 그대로 할 경우 발생하는 에러
            - 유저는 이미 퇴장했기 때문에 checkParticipants가 항상 예외를 던짐
            - CLIENT에서 profileID로 보내는 사람을 구분하기 때문에 퇴장 메세지 출력이 틀어짐
            이에 따라 날짜, 입장, 퇴장 등 모든 상태 메세지는 SYSTEM으로 변경해야 함
            주의할 점은 SYSTEM profile을 미리 만들어야 하고 SYSTEM 계정 프로필 상수도 지정해야 함
         */
        TextDTO text = TextDTO.builder()
                .profileId(SYSTEM_ID)
                .chatroomId(chatroom.getChatroomId().toString())
                .messageType(messageType)
                .content(message)
                .profiles(profiles)
                .build();

        return messageService.sendSystemText(text, SYSTEM, chatroom);
    }

    public List<TextDTO> makeWelcomeMessage(String profileId, String chatroomId) {

        //사용자 조회
        Profile profile = profileService.findProfile(profileId);

        //입장 메세지 생성
        String message = profile.getNickname() + WELCOME;

        //입장 처리
        ChatroomEntity chatroom = chatroomService.joinChatroom(profile, chatroomId);

        return preSendMessage(chatroom, MessageType.JOIN, message);
    }

    public List<TextDTO> makeDieMessage(String profileId, String chatroomId) {

        Profile profile = profileService.findProfile(profileId);
        log.info("DIE USER {} ", profile.getNickname());

        //퇴장 메세지 생성
        String message = profile.getNickname() + BYE;

        //퇴장 처리
        ChatroomEntity chatroom = chatroomService.dieChatroom(profile, chatroomId);

        return preSendMessage(chatroom, MessageType.DIE, message);
    }

    public List<TextDTO> makeDisableMessage(String profileId, String chatroomId) {

        //비활성화할 사용자 조회
        Profile profile = profileService.findProfile(profileId);
        log.info("DISABLE USER {} ", profile.getNickname());

        //퇴장 메세지 생성
        String message = profile.getNickname() + BYE;

        //비활성화 처리
        ChatroomEntity chatroom = chatroomService.disableChatroom(profile, chatroomId);

        return preSendMessage(chatroom, MessageType.DISABLE, message);
    }
}
