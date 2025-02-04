package org._1mg.tt_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.chat.entity.*;
import org._1mg.tt_backend.chat.exception.custom.ChatroomNotFoundException;
import org._1mg.tt_backend.chat.exception.custom.ProfileNotParticipants;
import org._1mg.tt_backend.chat.repository.*;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org._1mg.tt_backend.base.CustomException.CHATROOM_NOT_FOUND;
import static org._1mg.tt_backend.base.CustomException.USER_NOT_IN_CHATROOM;

/**
 * ProfileChatroomEntity 필드 등 여러 Service 클래스에서 사용되는 쿼리 집합(순환 참조 방지)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatUtils {

    private final ProfileChatroomRepository profileChatroomRepository;
    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;

    private final PrivateMessageRepository privateMessageRepository;
    private final PrivateChatroomRepository privateChatroomRepository;

    public MessageEntity getLastMessage(Long chatroom) {

        return messageRepository.findLastMessageWithChatroomNotDeleted(chatroom, Limit.of(1));
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
        return chatroomRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> new ChatroomNotFoundException(CHATROOM_NOT_FOUND.getMessage()));
    }


    public ProfileChatroomEntity checkParticipant(Long profileId, Long chatroomId) {

        ProfileChatroomEntity profileChatroom = profileChatroomRepository.findByProfileIdAndChatroomIdNotDeleted(profileId, chatroomId);
        if (profileChatroom == null) {
            throw new ProfileNotParticipants(USER_NOT_IN_CHATROOM.getMessage());
        }

        return profileChatroom;
    }

    public ProfileChatroomEntity getProfileChatroom(Long profileId, Long chatroomId) {

        return profileChatroomRepository.findByProfileIdAndChatroomIdWithDeleted(profileId, chatroomId);
    }

    public void join(ProfileChatroomEntity profileChatroom) {

        profileChatroomRepository.save(profileChatroom);
    }

    public List<ProfileChatroomEntity> getChatrooms(Long profileId) {

        return profileChatroomRepository.findChatroomsByProfileIdNotDeleted(profileId);
    }

    public void toNullSender(Long profileId) {

        //messageRepository.nullifySenderForMessages(memberId);
        List<ProfileChatroomEntity> chatrooms = profileChatroomRepository.findAllUserChatrooms(profileId)
                .stream()
                .peek(ProfileChatroomEntity::detachProfile)
                .toList();

        profileChatroomRepository.saveAll(chatrooms);
    }

    public boolean checkFirstPrivateMessage(Long chatroomId, LocalDateTime now) {

        PrivateMessageEntity lastMessage = getLastPrivateMessage(chatroomId);
        if (lastMessage == null) {
            return true;
        }
        return now.toLocalDate().isAfter(lastMessage.getCreatedAt().toLocalDate());
    }

    public PrivateMessageEntity getLastPrivateMessage(Long chatroomId) {

        return privateMessageRepository.findLastMessageWithChatroomNotDeleted(chatroomId, Limit.of(1));
    }

    public PrivateChatroomEntity findPrivateChatroom(Long profileId, String chatroomId) {

        Long id = Long.parseLong(chatroomId);
        return privateChatroomRepository.findByIdAndUserNotDeleted(profileId, id)
                .orElseThrow(() -> new ChatroomNotFoundException(CHATROOM_NOT_FOUND.getMessage()));
    }

    public List<ProfileChatroomEntity> getProfilesForNotification(String chatroomId) {

        return profileChatroomRepository.findProfileAndChatroomNotDeletedAndActive(Long.parseLong(chatroomId));
    }
}
