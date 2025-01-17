package org._1mg.tt_backend.chat.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org._1mg.tt_backend.chat.exception.AlreadyInChatroomException;
import org._1mg.tt_backend.chat.exception.ProfileNotParticipants;
import org._1mg.tt_backend.chat.repository.ProfileChatroomRepository;
import org.springframework.stereotype.Service;

import static org._1mg.tt_backend.base.CustomException.USER_ALREADY_IN_CHATROOM;
import static org._1mg.tt_backend.base.CustomException.USER_NOT_IN_CHATROOM;

@Service
@RequiredArgsConstructor
public class ProfileChatroomService {

    private final ProfileChatroomRepository profileChatroomRepository;


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
