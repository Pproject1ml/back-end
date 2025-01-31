package org._1mg.tt_backend.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.chat.dto.PrivateChatroomDTO;
import org._1mg.tt_backend.chat.entity.PrivateChatroomEntity;
import org._1mg.tt_backend.chat.exception.custom.ChatroomNotFoundException;
import org._1mg.tt_backend.chat.repository.PrivateChatroomRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org._1mg.tt_backend.base.CustomException.CHATROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PrivateChatroomService {

    private final PrivateChatroomRepository chatroomRepository;
    private final ProfileService profileService;
    final String DESTINATION = "/sub/private-room/";

    public String createPrivateChatroom(String user2MemberId, Profile user2) {

        Profile user1 = profileService.findProfileWithMemberId(user2MemberId);

        //일대 일 채팅방 생성
        PrivateChatroomEntity privateChatroom = chatroomRepository.save(
                PrivateChatroomEntity.builder()
                        .user1(user1)
                        .user2(user2)
                        .build());


        return DESTINATION + privateChatroom.getPrivateChatroomId();
    }

    public PrivateChatroomEntity findChatroom(Long profileId, String chatroomId) {

        Long id = Long.parseLong(chatroomId);
        return chatroomRepository.findByIdAndUserNotDeleted(profileId, id)
                .orElseThrow(() -> new ChatroomNotFoundException(CHATROOM_NOT_FOUND.getMessage()));
    }

    public List<PrivateChatroomDTO> getChatrooms(Long profileId) {

        List<PrivateChatroomDTO> chatrooms = new ArrayList<>();

        //active 정보를 받기 위해 ProfileChatroomEntity로 조회함
        List<PrivateChatroomEntity> chatroomList = chatroomRepository.findChatroomsByProfileIdNotDeleted(profileId);

        //chatrooms에 각각 접근 chatroom의 profiles 조회
        //chatrooms에 각각 접근 마지막 message 조회
        for (PrivateChatroomEntity chatroom : chatroomList) {


//            //chatroom 기본 정보 저장 + active 정보
//            PrivateChatroomDTO chatroomDTO = chatroom.convertToDTOForTab(chatroom.isAlarm());
//            Long chatroomId = chatroom.getPrivateChatroomId();
//
//            //chatroom 별 참가자 List 추가
//            chatroomDTO.setProfiles(profileDTOs);
//
//            //채팅방 별 마지막 메세지 조회
//            PrivateMessageEntity lastMessage = chatUtils.getLastMessage(chatroomId);
//
//            //마지막 메세지가 없는 경우엔 그냥 NULL로 놔두면 됨
//            if (lastMessage != null) {
//                chatroomDTO.setLastMessage(lastMessage.getContent());
//                chatroomDTO.setLastMessageAt(lastMessage.getCreatedAt());
//            }
//
//            //생성된 chatroomDTO를 List에 추가
//            chatrooms.add(chatroomDTO);
        }

        return chatrooms;
    }
}
