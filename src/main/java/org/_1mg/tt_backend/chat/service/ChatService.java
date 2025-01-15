package org._1mg.tt_backend.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.exception.member.custom.ProfileNotFoundException;
import org._1mg.tt_backend.auth.repository.ProfileRepository;
import org._1mg.tt_backend.chat.dto.*;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.entity.MessageEntity;
import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org._1mg.tt_backend.chat.exception.ChatroomNotFoundException;
import org._1mg.tt_backend.chat.exception.ProfileNotParticipants;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.chat.repository.MessageRepository;
import org._1mg.tt_backend.chat.repository.ProfileChatroomRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org._1mg.tt_backend.base.CustomException.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;
    private final ProfileRepository profileRepository;
    private final ProfileChatroomRepository profileChatroomRepository;

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

    public void joinChatroom(JoinDTO joinDTO) {

        Profile profile = findProfile(joinDTO.getProfileId());
        ChatroomEntity chatroom = findChatroom(joinDTO.getChatroomId());

        profileChatroomRepository.save(ProfileChatroomEntity.create(profile, chatroom));
    }

    public TextDTO sendText(TextDTO textDTO, Long chatroomId) {

        //profile 조회
        Profile profile = findProfile(textDTO.getProfileId());

        //chatroom 조회
        ChatroomEntity chatroom = findChatroom(textDTO.getChatroomId());

        //참가 여부 확인
        checkParticipant(profile.getProfileId(), chatroom.getChatroomId());

        //저장
        MessageEntity message = MessageEntity.create(chatroom, profile, textDTO.getMessageType(), textDTO.getContent());
        message = messageRepository.save(message);

        //createdAt 필드 초기화 후 반환
        textDTO.setMessageId(message.getMessageId().toString());
        textDTO.setCreatedAt(message.getCreatedAt());
        return textDTO;
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

    public ProfileChatroomEntity checkParticipant(Long profileId, Long chatroomId) {

        ProfileChatroomEntity profileChatroom = profileChatroomRepository.findByProfileIdAndChatroomId(profileId, chatroomId);
        if (profileChatroom == null) {
            throw new ProfileNotParticipants(USER_NOT_IN_CHATROOM.getMessage());
        }

        return profileChatroom;
    }

    public void leaveChatroom(LeaveDTO leaveDTO) {

        Profile profile = findProfile(leaveDTO.getProfileId());

        ChatroomEntity chatroom = findChatroom(leaveDTO.getChatroomId());

        ProfileChatroomEntity profileChatroom = checkParticipant(profile.getProfileId(), chatroom.getChatroomId());
        profileChatroom.leave();
    }

    public void dieChatroom(DieDTO dieDTO) {

        Profile profile = findProfile(dieDTO.getProfileId());

        ChatroomEntity chatroom = findChatroom(dieDTO.getProfileId());

        ProfileChatroomEntity profileChatroom = checkParticipant(profile.getProfileId(), chatroom.getChatroomId());
        profileChatroom.delete();
    }
}
