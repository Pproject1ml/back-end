package org._1mg.tt_backend.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.base.CustomException;
import org._1mg.tt_backend.chat.dto.PrivateChatroomDTO;
import org._1mg.tt_backend.chat.entity.PrivateChatroomEntity;
import org._1mg.tt_backend.chat.entity.PrivateMessageEntity;
import org._1mg.tt_backend.chat.exception.custom.AlreadyInChatroomException;
import org._1mg.tt_backend.chat.repository.PrivateChatroomRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PrivateChatroomService {

    private final PrivateChatroomRepository chatroomRepository;
    private final ProfileService profileService;
    private final ChatUtils chatUtils;

    public PrivateChatroomDTO createPrivateChatroom(String user1MemberId, Profile user2) {

        Profile user1 = profileService.findProfileWithMemberId(user1MemberId);
        PrivateChatroomEntity privateChatroom = checkAlreadyParticipants(user1, user2);

        if (privateChatroom == null) {
            //일대 일 채팅방 생성
            privateChatroom = chatroomRepository.save(PrivateChatroomEntity.create(user1, user2));
        }

        PrivateChatroomDTO dto = privateChatroom.convertToDTO();
        dto.setProfiles(List.of(user1.convertToDTO(), user2.convertToDTO()));

        return dto;
    }

    public PrivateChatroomEntity checkAlreadyParticipants(Profile user1, Profile user2) {

        PrivateChatroomEntity pc = chatroomRepository.findByUser1AndUser2(user1.getProfileId(), user2.getProfileId());

        if (pc == null) {
            return null;
        }

        if (pc.isDeleted()) {
            pc.restore();
            return pc;
        } else {
            throw new AlreadyInChatroomException(CustomException.USER_ALREADY_IN_CHATROOM.getMessage());
        }
    }

    public List<PrivateChatroomDTO> getChatrooms(Long profileId) {

        List<PrivateChatroomDTO> privateChatrooms = new ArrayList<>();

        //active 정보를 받기 위해 ProfileChatroomEntity로 조회함
        List<PrivateChatroomEntity> chatroomList = chatroomRepository.findChatroomsByProfileIdNotDeleted(profileId);

        //chatrooms에 각각 접근 chatroom의 profiles 조회
        //chatrooms에 각각 접근 마지막 message 조회
        for (PrivateChatroomEntity chatroom : chatroomList) {

            PrivateChatroomDTO chatroomDTO = chatroom.convertToDTO();

            //chatroom 별 참가자 List 추가
            chatroomDTO.setProfiles(List.of(chatroom.getUser1().convertToDTO(), chatroom.getUser2().convertToDTO()));

            //채팅방 별 마지막 메세지 조회
            Long chatroomId = chatroom.getPrivateChatroomId();
            PrivateMessageEntity lastMessage = chatUtils.getLastPrivateMessage(chatroomId);

            //마지막 메세지가 없는 경우엔 그냥 NULL로 놔두면 됨
            if (lastMessage != null) {
                chatroomDTO.setLastMessage(lastMessage.getContent());
                chatroomDTO.setLastMessageAt(lastMessage.getCreatedAt());
            }

            //생성된 chatroomDTO를 List에 추가
            privateChatrooms.add(chatroomDTO);
        }

        return privateChatrooms;
    }

    public void toNullSender(Long profileId) {

        //messageRepository.nullifySenderForMessages(memberId);
        List<PrivateChatroomEntity> chatrooms = chatroomRepository.findAllUserPrivateChatrooms(profileId)
                .stream()
                .peek(chatroom -> chatroom.detachProfile(profileId))
                .toList();

        chatroomRepository.saveAll(chatrooms);
    }

    public List<Profile> getProfileForNotification(String chatroomId) {

        PrivateChatroomEntity chatroom = chatroomRepository.findProfilesById(Long.parseLong(chatroomId));
        return List.of(chatroom.getUser1(), chatroom.getUser2());
    }
}
