package org._1mg.tt_backend.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.repository.ProfileRepository;
import org._1mg.tt_backend.chat.dto.AlarmDTO;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.entity.MessageEntity;
import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org._1mg.tt_backend.chat.exception.ChatroomNotFoundException;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.chat.repository.MessageRepository;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org._1mg.tt_backend.base.CustomException.CHATROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final ProfileRepository profileRepository;
    private final MessageRepository messageRepository;
    private final ProfileChatroomService profileChatroomService;

    public ChatroomEntity findChatroom(String chatroomId) {

        Long id = Long.parseLong(chatroomId);
        return chatroomRepository.findById(id)
                .orElseThrow(() -> new ChatroomNotFoundException(CHATROOM_NOT_FOUND.getMessage()));
    }

    /**
     * 랜드마크와 연관된 채팅방을 생성합니다.
     */
    public ChatroomEntity createChatroomForLandmark(Landmark landmark) {

        String chatroomTitle = "Chatroom for " + landmark.getName();
        return chatroomRepository.save(ChatroomEntity.create(chatroomTitle));
    }

    /**
     * 사용자가 참가 중인 chatroom 정보와 그 chatroom의 참가자 정보 조회
     */
    public List<ChatroomDTO> getChatrooms(Long profileId) {

        List<ChatroomDTO> chatrooms = new ArrayList<>();

        //profileId에 해당하는 chatrooms 조회
        List<ChatroomEntity> chatroomEntityList = chatroomRepository.findChatroomsByProfileId(profileId);
        /*
         * 필요한 예외
         * - profileId에 해당하는 chatroom이 존재하지 않는 겯우
         * - chatroom에 본인 외에 profile이 존재하지 않는 경우
         * - chatroom에 해당하는 landmark가 존재하지 않는 경우
         */


        //chatrooms에 각각 접근 chatroom의 profiles 조회
        //chatrooms에 각각 접근 마지막 message 조회
        for (ChatroomEntity chatroomEntity : chatroomEntityList) {

            //chatroom 기본 정보 저장
            ChatroomDTO chatroomDTO = chatroomEntity.convertToDTOForTab();
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

    public void changeAlarm(AlarmDTO alarmDTO) {

        Long profileId = Long.parseLong(alarmDTO.getProfileId());
        Long chatroomId = Long.parseLong(alarmDTO.getChatroomId());

        ProfileChatroomEntity profileChatroom = profileChatroomService.checkParticipant(profileId, chatroomId);
        profileChatroom.changeAlarm(alarmDTO.isAlarm());
    }
}
