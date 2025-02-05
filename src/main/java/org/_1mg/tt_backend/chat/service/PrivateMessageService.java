package org._1mg.tt_backend.chat.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.EnterDTO;
import org._1mg.tt_backend.chat.dto.LeaveDTO;
import org._1mg.tt_backend.chat.dto.RefreshDTO;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org._1mg.tt_backend.chat.entity.PrivateChatroomEntity;
import org._1mg.tt_backend.chat.entity.PrivateMessageEntity;
import org._1mg.tt_backend.chat.repository.PrivateMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PrivateMessageService {

    private final ProfileService profileService;
    private final PrivateMessageRepository messageRepository;
    private final ChatUtils chatUtils;

    @Value("${chat.system}")
    private String SYSTEM_ID;
    private Profile SYSTEM;

    @PostConstruct
    public void initSystem() {
        SYSTEM = profileService.findProfile(SYSTEM_ID);
    }

    public List<TextDTO> getMessagesByRange(RefreshDTO refreshDTO) {

        Long start = refreshDTO.getStart();
        Long end = refreshDTO.getEnd();
        Long chatroom = refreshDTO.getChatroom();

        if (start == null) {

            if (end == null) {
                return messageRepository.findAllMessagesAtChatroom(chatroom)
                        .stream()
                        .map(PrivateMessageEntity::convertToText)
                        .toList();
            }

            return messageRepository.findMessagesUntilEnd(chatroom, end)
                    .stream()
                    .map(PrivateMessageEntity::convertToText)
                    .toList();
        }

        if (end == null) {
            return messageRepository.findMessagesFromStartNotDeleted(chatroom, start)
                    .stream()
                    .map(PrivateMessageEntity::convertToText)
                    .toList();
        }

        return messageRepository.findMessagesBetweenStartAndEndNotDeleted(chatroom, start, end)
                .stream()
                .map(PrivateMessageEntity::convertToText)
                .toList();
    }

    public List<TextDTO> sendText(TextDTO textDTO) {

        List<TextDTO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        log.info(now.toString());

        //profile 조회
        Profile profile = profileService.findProfile((textDTO.getProfileId()));
        log.info("profile : {} ", profile.getProfileId());


        //chatroom 조회 및 참가 여부 확인
        PrivateChatroomEntity chatroom = chatUtils.findPrivateChatroom(profile.getProfileId(), textDTO.getChatroomId());
        log.info("chatroom : {} ", chatroom.getPrivateChatroomId());

        //메세지 생성
        makeMessages(profile, chatroom, now, textDTO, result);
        log.info("result : {} ", result.get(result.size() - 1));

        return result;
    }

    public void makeMessages(Profile profile, PrivateChatroomEntity chatroom, LocalDateTime now, TextDTO textDTO, List<TextDTO> result) {

        //오늘의 첫 메세지인지 확인
        if (chatUtils.checkFirstPrivateMessage(chatroom.getPrivateChatroomId(), now)) {

            //날짜 데이터의 경우 SYSTEM 계정으로 메세지 작성;
            PrivateMessageEntity date = PrivateMessageEntity.create(chatroom, SYSTEM, MessageType.DATE, now.toString());
            messageRepository.save(date);
            result.add(date.convertToText());

            log.info(date.getContent());
        }

        //저장
        PrivateMessageEntity message = PrivateMessageEntity.create(chatroom, profile, textDTO.getMessageType(), textDTO.getContent());
        message = messageRepository.save(message);
        log.info(message.getContent());

        //createdAt 필드 초기화 후 반환
        textDTO.setMessageId(message.getPrivateMessageId().toString());
        textDTO.setCreatedAt(message.getCreatedAt());
        result.add(textDTO);
    }

    public void toNullSender(Long profileId) {

        //messageRepository.nullifySenderForMessages(memberId);
        List<PrivateMessageEntity> messages = messageRepository.findAllMessages(profileId).stream()
                .peek(PrivateMessageEntity::detachProfile)
                .toList();

        messageRepository.saveAll(messages);
    }

    public void leaveChatroom(LeaveDTO leaveDTO) {

        Profile profile = profileService.findProfile(leaveDTO.getProfileId());
        PrivateChatroomEntity privateChatroom = chatUtils.findPrivateChatroomAndProfile(profile.getProfileId(), leaveDTO.getChatroomId());

        if (profile.getProfileId().equals(privateChatroom.getUser1().getProfileId())) {
            privateChatroom.leaveUser1();
        }

        if (profile.getProfileId().equals(privateChatroom.getUser2().getProfileId())) {
            privateChatroom.leaveUser2();
        }
    }

    public void enterChatroom(EnterDTO enterDTO) {

        Profile profile = profileService.findProfile(enterDTO.getProfileId());
        PrivateChatroomEntity privateChatroom = chatUtils.findPrivateChatroomAndProfile(profile.getProfileId(), enterDTO.getChatroomId());

        if (profile.getProfileId().equals(privateChatroom.getUser1().getProfileId())) {
            privateChatroom.enterUser1();
        }

        if (profile.getProfileId().equals(privateChatroom.getUser2().getProfileId())) {
            privateChatroom.enterUser2();
        }
    }
}
