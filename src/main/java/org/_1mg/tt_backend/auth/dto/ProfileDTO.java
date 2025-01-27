package org._1mg.tt_backend.auth.dto;

import lombok.*;
import org._1mg.tt_backend.auth.entity.Gender;
import org._1mg.tt_backend.auth.entity.Profile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

    private String profileId;
    private String nickname;
    private String email;
    private String profileImage;
    private String introduction;
    private Integer age;
    private Gender gender;
    private Boolean isVisible;
    private String fcmToken; // fcm 토큰 추가


    public ProfileDTO checkNull(ProfileDTO profileDTO, Profile profile) {

        return ProfileDTO.builder()
                .profileId(profileDTO.profileId == null ? profile.getProfileId().toString() : profileDTO.profileId)
                .nickname(profileDTO.nickname == null ? profile.getNickname() : profileDTO.nickname)
                .email(profileDTO.email == null ? profile.getEmail() : profileDTO.email)
                .profileImage(profileDTO.profileImage == null ? profile.getProfileImage() : profileDTO.profileImage)
                .introduction(profileDTO.introduction == null ? profile.getIntroduction() : profileDTO.introduction)
                .age(profileDTO.age == null ? profile.getAge() : profileDTO.age)
                .gender(profileDTO.gender == null ? profile.getGender() : profileDTO.gender)
                .isVisible(profileDTO.isVisible == null ? profile.isVisible() : profileDTO.isVisible)
                .fcmToken(profileDTO.fcmToken == null ? profile.getFcmToken() : profileDTO.fcmToken) // 추가
                .build();
    }
}
