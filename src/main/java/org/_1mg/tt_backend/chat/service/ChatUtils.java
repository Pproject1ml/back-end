package org._1mg.tt_backend.chat.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.entity.MessageEntity;
import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org._1mg.tt_backend.chat.exception.AlreadyInChatroomException;
import org._1mg.tt_backend.chat.exception.ChatroomNotFoundException;
import org._1mg.tt_backend.chat.exception.ProfileNotParticipants;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.chat.repository.MessageRepository;
import org._1mg.tt_backend.chat.repository.ProfileChatroomRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org._1mg.tt_backend.base.CustomException.*;

/**
 * ProfileChatroomEntity 필드 등 여러 Service 클래스에서 사용되는 쿼리 집합(순환 참조 방지)
 */
@Service
@RequiredArgsConstructor
public class ChatUtils {

    private final ProfileChatroomRepository profileChatroomRepository;
    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;

    public MessageEntity getLastMessage(Long chatroom) {

        return messageRepository.findLastMessageWithChatroom(chatroom, Limit.of(1));
    }

    public boolean checkFirstMessage(Long chatroomId, LocalDateTime now) {

        MessageEntity lastMessage = getLastMessage(chatroomId);
        if (lastMessage == null) {
            return true;
        }
        return now.toLocalDate().isAfter(lastMessage.getCreatedAt().toLocalDate());
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

    public void checkAlreadyIn(Long profileId, Long chatroomId) {

        ProfileChatroomEntity profileChatroom = profileChatroomRepository.findByProfileIdAndChatroomId(profileId, chatroomId);
        if (profileChatroom != null) {
            throw new AlreadyInChatroomException(USER_ALREADY_IN_CHATROOM.getMessage());
        }
    }

    public void join(ProfileChatroomEntity profileChatroom) {

        profileChatroomRepository.save(profileChatroom);
    }
}
