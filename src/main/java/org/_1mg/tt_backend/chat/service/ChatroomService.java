package org._1mg.tt_backend.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.chat.dto.AlarmDTO;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;
import org._1mg.tt_backend.chat.dto.DieDTO;
import org._1mg.tt_backend.chat.dto.JoinDTO;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.entity.MessageEntity;
import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final ChatUtils chatUtils;
    private final ProfileService profileService;

    /**
     * 랜드마크와 연관된 채팅방을 생성합니다.
     */
    public ChatroomEntity createChatroomForLandmark(Landmark landmark) {

        //Landmark 이름과 채팅방 이름 통일
        String chatroomTitle = landmark.getName();
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
            List<ProfileDTO> profileDTOs = profileService.findProfiles(chatroomId);

            //chatroom 별 참가자 List 추가
            chatroomDTO.setProfiles(profileDTOs);

            //채팅방 별 마지막 메세지 조회
            MessageEntity lastMessage = chatUtils.getLastMessage(chatroomId);

            //마지막 메세지가 없는 경우엔 그냥 NULL로 놔두면 됨
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

        ProfileChatroomEntity profileChatroom = chatUtils.checkParticipant(profileId, chatroomId);
        profileChatroom.changeAlarm(alarmDTO.isAlarm());
    }

    public String joinChatroom(JoinDTO joinDTO) {

        Profile profile = profileService.findProfile(joinDTO.getProfileId());
        ChatroomEntity chatroom = chatUtils.findChatroom(joinDTO.getChatroomId());
        chatroom.join();

        chatUtils.checkAlreadyIn(profile.getProfileId(), chatroom.getChatroomId());
        chatUtils.join(ProfileChatroomEntity.create(profile, chatroom));

        return profile.getNickname();
    }

    public void dieChatroom(DieDTO dieDTO) {

        Profile profile = profileService.findProfile((dieDTO.getProfileId()));

        ChatroomEntity chatroom = chatUtils.findChatroom((dieDTO.getProfileId()));
        chatroom.die();

        ProfileChatroomEntity profileChatroom = chatUtils.checkParticipant(profile.getProfileId(), chatroom.getChatroomId());
        profileChatroom.delete();
    }
}
