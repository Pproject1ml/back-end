package org._1mg.tt_backend.chat.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;

    /**
     * 랜드마크와 연관된 채팅방을 생성합니다.
     */
    public ChatroomEntity createChatroomForLandmark(Landmark landmark) {
        String chatroomTitle = "Chatroom for " + landmark.getName();
        ChatroomEntity chatroom = ChatroomEntity.builder()
                .title(chatroomTitle)
                .build();
        return chatroomRepository.save(chatroom);
    }
}
