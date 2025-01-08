package org._1mg.tt_backend.chat.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.repository.ProfileRepository;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.entity.MessageEntity;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.chat.repository.MessageRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatroomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final ProfileRepository profileRepository;

    public List<ChatroomDTO> getChatrooms(Long profileId) {

        List<ChatroomDTO> chatrooms = new ArrayList<>();

        //profileId에 해당하는 chatrooms 조회
        List<ChatroomEntity> chatroomEntityList = chatRoomRepository.findChatroomsByProfileId(profileId);


        //chatrooms에 각각 접근 chatroom의 profiles 조회
        //chatrooms에 각각 접근 마지막 message 조회
        for (ChatroomEntity chatroomEntity : chatroomEntityList) {

            //chatroom 기본 정보 저장
            ChatroomDTO chatroomDTO = chatroomEntity.convertToDTOWithChatroomInfo();

            Long chatroomId = chatroomEntity.getChatroomId();

            //chatroom 참가자 조회
            //각 chatroom 별 참가자 List 생성
            List<ProfileDTO> profileDTOs = profileRepository.findProfilesByChatroomId(chatroomId)
                    .stream()
                    .map(Profile::convertToDTO)
                    .toList();

            //chatroom 별 참가자 List를 Map에 저장
            Map<Long, List<ProfileDTO>> tmp = Map.of(chatroomEntity.getChatroomId(), profileDTOs);
            chatroomDTO.setProfiles(tmp);

            //채팅방 별 마지막 메세지 조회
            MessageEntity lastMessage = messageRepository.findLastMessageWithChatroom(chatroomId, Limit.of(1));
            chatroomDTO.setLastMessage(lastMessage.getContent());
            chatroomDTO.setLastMessageAt(lastMessage.getCreatedAt());

            //생성된 chatroomDTO를 List에 추가
            chatrooms.add(chatroomDTO);
        }

        return chatrooms;
    }
}
