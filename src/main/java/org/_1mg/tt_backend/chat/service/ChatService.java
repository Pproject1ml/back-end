package org._1mg.tt_backend.chat.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.repository.ProfileRepository;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;
import org._1mg.tt_backend.chat.dto.JoinDTO;
import org._1mg.tt_backend.chat.dto.MessageDTO;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.entity.MessageEntity;
import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.chat.repository.MessageRepository;
import org._1mg.tt_backend.chat.repository.ProfileChatroomRepository;
import org._1mg.tt_backend.exception.chat.ChatroomNotFoundException;
import org._1mg.tt_backend.exception.member.ProfileNotFoundException;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatroomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final ProfileRepository profileRepository;
    private final ProfileChatroomRepository profileChatroomRepository;

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

            //chatroom 별 참가자 List 추가
            chatroomDTO.setProfiles(profileDTOs);

            //채팅방 별 마지막 메세지 조회
            MessageEntity lastMessage = messageRepository.findLastMessageWithChatroom(chatroomId, Limit.of(1));
            //채팅방에 메세지가 없는 경우에 대한 처리
            //이 부분에 대한 예외 처리 필요? 아니면 그냥 NULL로 넘겨도 상관없지 않나..?
            if (lastMessage != null) {
                chatroomDTO.setLastMessage(lastMessage.getContent());
                chatroomDTO.setLastMessageAt(lastMessage.getCreatedAt());
            }

            //생성된 chatroomDTO를 List에 추가
            chatrooms.add(chatroomDTO);
        }

        return chatrooms;
    }

    public List<MessageDTO> getMessagesByRange(Long chatroomId, Long start, Long end) {

        /*
         chatroom에 startId와 endId 사이의 메세지와 그 메세지를 보낸 Profile을 조회
         start : APP Local storage에 저장되어 있는 마지막 메세지
         end : STOMP Socket 연결 이후 APP cache에 들어가는 첫 메세지
         */
        return messageRepository.findMessagesByChatroomIdAndIdRange(chatroomId, start, end)
                .stream()
                .map(MessageEntity::convertToDTO)
                .toList();
    }

    public ProfileDTO joinChatroom(JoinDTO joinDTO, Long chatroomId) {

        Long profileId = Long.parseLong(joinDTO.getProfileId());
        Profile profile = profileRepository.findById(profileId).orElseThrow(() ->
                new ProfileNotFoundException(profileId + " NOT FOUND")
        );

        ChatroomEntity chatroom = chatRoomRepository.findById(chatroomId).orElseThrow(() ->
                new ChatroomNotFoundException(chatroomId + " NOT FOUND")
        );

        profileChatroomRepository.save(ProfileChatroomEntity.create(profile, chatroom));

        return profile.convertToDTO();
    }
}
