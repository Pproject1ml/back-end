package org._1mg.tt_backend.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.exception.member.custom.ProfileNotFoundException;
import org._1mg.tt_backend.auth.repository.ProfileRepository;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.*;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.entity.MessageEntity;
import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org._1mg.tt_backend.chat.exception.ChatroomNotFoundException;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.chat.repository.MessageRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org._1mg.tt_backend.base.CustomException.CHATROOM_NOT_FOUND;
import static org._1mg.tt_backend.base.CustomException.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;
    private final ProfileRepository profileRepository;
    private final ProfileChatroomService profileChatroomService;

    public List<TextDTO> getMessagesByRange(RefreshDTO refreshDTO) {

        /*
         chatroom에 startId와 endId 사이의 메세지와 그 메세지를 보낸 Profile을 조회
         start : APP Local storage에 저장되어 있는 마지막 메세지
         end : STOMP Socket 연결 이후 APP cache에 들어가는 첫 메세지
         */

        Long start = refreshDTO.getStart();
        Long end = refreshDTO.getEnd();
        Long chatroom = refreshDTO.getChatroom();

        if (start == null) {
            return new ArrayList<>();
        }

        if (end == null) {
            return messageRepository.findMessagesFromStart(chatroom, start)
                    .stream()
                    .map(MessageEntity::convertToText)
                    .toList();
        }

        return messageRepository.findMessagesBetweenStartAndEnd(chatroom, start, end)
                .stream()
                .map(MessageEntity::convertToText)
                .toList();
    }

    public String joinChatroom(JoinDTO joinDTO) {

        Profile profile = findProfile(joinDTO.getProfileId());
        ChatroomEntity chatroom = findChatroom(joinDTO.getChatroomId());
        chatroom.join();

        profileChatroomService.checkAlreadyIn(profile.getProfileId(), chatroom.getChatroomId());
        profileChatroomService.join(ProfileChatroomEntity.create(profile, chatroom));

        return profile.getNickname();
    }

    public boolean checkFirstMessage(Long chatroomId, LocalDateTime now) {

        MessageEntity lastMessage = messageRepository.findLastMessageWithChatroom(chatroomId, Limit.of(1));
        if (lastMessage == null) {
            return true;
        }
        return now.toLocalDate().isAfter(lastMessage.getCreatedAt().toLocalDate());
    }

    public List<TextDTO> sendText(TextDTO textDTO, Long chatroomId) {

        List<TextDTO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        //profile 조회
        Profile profile = findProfile(textDTO.getProfileId());

        //chatroom 조회
        ChatroomEntity chatroom = findChatroom(textDTO.getChatroomId());

        //참가 여부 확인
        profileChatroomService.checkParticipant(profile.getProfileId(), chatroom.getChatroomId());

        //오늘의 첫 메세지인지 확인
        if (checkFirstMessage(chatroomId, now)) {

            MessageEntity date = MessageEntity.create(chatroom, profile, MessageType.DATE, now.toString());
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

        return result;
    }

    public Profile findProfile(String profileId) {

        Long id = Long.parseLong(profileId);
        return profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(USER_NOT_FOUND.getMessage()));
    }

    public ChatroomEntity findChatroom(String chatroomId) {

        Long id = Long.parseLong(chatroomId);
        return chatroomRepository.findById(id)
                .orElseThrow(() -> new ChatroomNotFoundException(CHATROOM_NOT_FOUND.getMessage()));
    }


    public void leaveChatroom(LeaveDTO leaveDTO) {

        Profile profile = findProfile(leaveDTO.getProfileId());

        ChatroomEntity chatroom = findChatroom(leaveDTO.getChatroomId());

        ProfileChatroomEntity profileChatroom = profileChatroomService.checkParticipant(profile.getProfileId(), chatroom.getChatroomId());
        profileChatroom.leave();
    }

    public void dieChatroom(DieDTO dieDTO) {

        Profile profile = findProfile(dieDTO.getProfileId());

        ChatroomEntity chatroom = findChatroom(dieDTO.getProfileId());
        chatroom.die();

        ProfileChatroomEntity profileChatroom = profileChatroomService.checkParticipant(profile.getProfileId(), chatroom.getChatroomId());
        profileChatroom.delete();
    }
}
