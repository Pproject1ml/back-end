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
import org._1mg.tt_backend.chat.exception.custom.AlreadyInChatroomException;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org._1mg.tt_backend.base.CustomException.USER_ALREADY_IN_CHATROOM;

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
        /*
         * 발생 가능한 예외
         * - profileId에 해당하는 chatroom이 존재하지 않는 겯우 : 그냥 빈 배열 반환
         * - chatroom에 본인 외에 profile이 존재하지 않는 경우 : 그냥 빈 배열 반환
         * - chatroom에 해당하는 landmark가 존재하지 않는 경우 : 그냥 join 자체가 안되서 null 값 반환함
         */

        //active 정보를 받기 위해 ProfileChatroomEntity로 조회함
        List<ProfileChatroomEntity> chatroomList = chatUtils.getChatrooms(profileId);

        //chatrooms에 각각 접근 chatroom의 profiles 조회
        //chatrooms에 각각 접근 마지막 message 조회
        for (ProfileChatroomEntity chatroomEntity : chatroomList) {

            //chatroom 기본 정보 저장 + active 정보
            ChatroomDTO chatroomDTO = chatroomEntity.getChatroom().convertToDTOForTab(chatroomEntity.isActive());
            Long chatroomId = chatroomEntity.getChatroom().getChatroomId();

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

    public void joinChatroom(JoinDTO joinDTO) {

        Profile profile = profileService.findProfile(joinDTO.getProfileId());
        ChatroomEntity chatroom = chatUtils.findChatroom(joinDTO.getChatroomId());
        ProfileChatroomEntity profileChatroom = chatUtils.getProfileChatroom(profile.getProfileId(), chatroom.getChatroomId());

        //아예 없는 경우 최초 생성
        if (profileChatroom == null) {
            chatUtils.join(ProfileChatroomEntity.create(profile, chatroom));
            return;
        }

        //이전에 나가기(삭제)됐던 경우
        if (profileChatroom.isDeleted()) {
            profileChatroom.restore();
            return;
        }

        //이전에 범위를 벗어난 경우
        if (!profileChatroom.isActive()) {
            profileChatroom.enable();
            return;
        }

        //있는데 나간적도 없고 비활성화도 안된 경우 던지는 예외
        throw new AlreadyInChatroomException(USER_ALREADY_IN_CHATROOM.getMessage());
    }

    public void disableChatroom(DieDTO dieDTO) {

        Profile profile = profileService.findProfile((dieDTO.getProfileId()));
        ChatroomEntity chatroom = chatUtils.findChatroom((dieDTO.getChatroomId()));
        ProfileChatroomEntity profileChatroom = chatUtils.checkParticipant(profile.getProfileId(), chatroom.getChatroomId());

        profileChatroom.disable();
    }

    public void dieChatroom(DieDTO dieDTO) {

        Profile profile = profileService.findProfile((dieDTO.getProfileId()));
        ChatroomEntity chatroom = chatUtils.findChatroom((dieDTO.getChatroomId()));
        ProfileChatroomEntity profileChatroom = chatUtils.checkParticipant(profile.getProfileId(), chatroom.getChatroomId());

        profileChatroom.die();
    }
}
