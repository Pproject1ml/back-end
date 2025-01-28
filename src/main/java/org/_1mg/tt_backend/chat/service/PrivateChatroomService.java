package org._1mg.tt_backend.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.service.ProfileService;
import org._1mg.tt_backend.chat.entity.PrivateChatroomEntity;
import org._1mg.tt_backend.chat.repository.PrivateChatroomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PrivateChatroomService {

    private final PrivateChatroomRepository privateChatroomRepository;
    private final ProfileService profileService;
    final String DESTINATION = "/sub/chat/";

    public String createPrivateChatroom(String user2MemberId, Profile user2) {

        Profile user1 = profileService.findProfileWithMemberId(user2MemberId);

        PrivateChatroomEntity privateChatroom = privateChatroomRepository.save(
                PrivateChatroomEntity.builder()
                        .user1(user1)
                        .user2(user2)
                        .build());

        return DESTINATION + privateChatroom.getPrivateChatroomId();
    }
}
