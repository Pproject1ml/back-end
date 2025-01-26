package org._1mg.tt_backend.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.EnterDTO;
import org._1mg.tt_backend.chat.dto.LeaveDTO;
import org._1mg.tt_backend.chat.dto.RefreshDTO;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.entity.MessageEntity;
import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org._1mg.tt_backend.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ProfileService profileService;
    private final ChatUtils chatUtils;

    @Value("${chat.system}")
    private String SYSTEM;

    /**
     * chatroom에 startId와 endId 사이의 메세지와 그 메세지를 보낸 Profile을 조회
     * start : APP Local storage에 저장되어 있는 마지막 메세지
     * end : STOMP Socket 연결 이후 APP cache에 들어가는 첫 메세지
     */
    public List<TextDTO> getMessagesByRange(RefreshDTO refreshDTO) {

        Long start = refreshDTO.getStart();
        Long end = refreshDTO.getEnd();
        Long chatroom = refreshDTO.getChatroom();

        if (start == null) {
            return new ArrayList<>();
        }

        if (end == null) {
            return messageRepository.findMessagesFromStartNotDeleted(chatroom, start)
                    .stream()
                    .map(MessageEntity::convertToText)
                    .toList();
        }

        return messageRepository.findMessagesBetweenStartAndEndNotDeleted(chatroom, start, end)
                .stream()
                .map(MessageEntity::convertToText)
                .toList();
    }

    public List<TextDTO> sendText(TextDTO textDTO) {

        List<TextDTO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        //profile 조회
        Profile profile = profileService.findProfile((textDTO.getProfileId()));

        //chatroom 조회
        ChatroomEntity chatroom = chatUtils.findChatroom((textDTO.getChatroomId()));

        //참가 여부 확인
        chatUtils.checkParticipant(profile.getProfileId(), chatroom.getChatroomId());

        //메세지 생성
        makeMessages(profile, chatroom, now, textDTO, result);

        return result;
    }

    public List<TextDTO> sendSystemText(TextDTO textDTO, ChatroomEntity chatroom) {

        List<TextDTO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        //profile 조회
        Profile profile = profileService.findProfile((textDTO.getProfileId()));

        //System 메세지는 참가하지 않아도 어디서나 보낼 수 있음
        //메세지 생성
        makeMessages(profile, chatroom, now, textDTO, result);

        return result;
    }

    public void makeMessages(Profile profile, ChatroomEntity chatroom, LocalDateTime now, TextDTO textDTO, List<TextDTO> result) {

        //오늘의 첫 메세지인지 확인
        if (chatUtils.checkFirstMessage(chatroom.getChatroomId(), now)) {

            //날짜 데이터의 경우 SYSTEM 계정으로 메세지 작성
            Profile system = profileService.findProfile(SYSTEM);
            MessageEntity date = MessageEntity.create(chatroom, system, MessageType.DATE, now.toString());
            messageRepository.save(date);
            result.add(date.convertToText());
        }

        //저장
        MessageEntity message = MessageEntity.create(chatroom, profile, textDTO.getMessageType(), textDTO.getContent());
        message = messageRepository.save(message);

        //createdAt 필드 초기화 후 반환
        textDTO.setMessageId(message.getMessageId().toString());
        textDTO.setCreatedAt(message.getCreatedAt());
        result.add(textDTO);
    }

    public void leaveChatroom(LeaveDTO leaveDTO) {

        Profile profile = profileService.findProfile((leaveDTO.getProfileId()));

        ChatroomEntity chatroom = chatUtils.findChatroom((leaveDTO.getChatroomId()));

        ProfileChatroomEntity profileChatroom = chatUtils.checkParticipant(profile.getProfileId(), chatroom.getChatroomId());
        profileChatroom.leave();
    }

    public void enterChatroom(EnterDTO enterDTO) {

    }
}
