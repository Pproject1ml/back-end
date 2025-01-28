package org._1mg.tt_backend.chat.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org._1mg.tt_backend.chat.entity.PrivateChatroomEntity;
import org._1mg.tt_backend.chat.entity.PrivateMessageEntity;
import org._1mg.tt_backend.chat.repository.PrivateMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PrivateMessageService {

    private final ProfileService profileService;
    private final PrivateMessageRepository messageRepository;

    @Value("${chat.system}")
    private String SYSTEM_ID;
    private Profile SYSTEM;

    @PostConstruct
    public void initSystem() {
        SYSTEM = profileService.findProfile(SYSTEM_ID);
    }

    public List<TextDTO> sendSystemText(TextDTO textDTO, Profile system, PrivateChatroomEntity chatroom) {

        List<TextDTO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        //System 메세지는 참가하지 않아도 어디서나 보낼 수 있음
        //메세지 생성
        makeMessages(system, chatroom, now, textDTO, result);

        return result;
    }

    public void makeMessages(Profile profile, PrivateChatroomEntity chatroom, LocalDateTime now, TextDTO textDTO, List<TextDTO> result) {

        //오늘의 첫 메세지인지 확인
        if (checkFirstMessage(chatroom.getPrivateChatroomId(), now)) {

            //날짜 데이터의 경우 SYSTEM 계정으로 메세지 작성;
            PrivateMessageEntity date = PrivateMessageEntity.create(chatroom, SYSTEM, MessageType.DATE, now.toString());
            messageRepository.save(date);
            result.add(date.convertToText());
        }

        //저장
        PrivateMessageEntity message = PrivateMessageEntity.create(chatroom, profile, textDTO.getMessageType(), textDTO.getContent());
        message = messageRepository.save(message);

        //createdAt 필드 초기화 후 반환
        textDTO.setMessageId(message.getPrivateMessageId().toString());
        textDTO.setCreatedAt(message.getCreatedAt());
        result.add(textDTO);
    }

    public boolean checkFirstMessage(Long chatroomId, LocalDateTime now) {

        PrivateMessageEntity lastMessage = getLastMessage(chatroomId);
        if (lastMessage == null) {
            return true;
        }
        return now.toLocalDate().isAfter(lastMessage.getCreatedAt().toLocalDate());
    }

    public PrivateMessageEntity getLastMessage(Long chatroom) {

        return null;
    }
}
