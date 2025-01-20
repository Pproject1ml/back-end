package org._1mg.tt_backend.auth.service;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.entity.Profile;
import org._1mg.tt_backend.auth.exception.member.custom.NicknameAlreadyExistsException;
import org._1mg.tt_backend.auth.exception.member.custom.ProfileNotFoundException;
import org._1mg.tt_backend.auth.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org._1mg.tt_backend.base.CustomException.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public void checkUniqueNickname(String nickname) {

        Profile profile = profileRepository.findByNickname(nickname);
        if (profile != null) {
            throw new NicknameAlreadyExistsException("NOT UNIQUE NICKNAME", nickname);
        }
    }
    
    public Profile findProfile(String profileId) {

        Long id = Long.parseLong(profileId);
        return profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(USER_NOT_FOUND.getMessage()));
    }

    public List<ProfileDTO> findProfiles(Long chatroomId) {

        return profileRepository.findProfilesByChatroomId(chatroomId)
                .stream()
                .map(Profile::convertToDTO)
                .toList();
    }

}
