package org._1mg.tt_backend.auth.dto;

import lombok.*;
import org._1mg.tt_backend.auth.entity.Profile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

    private String memberId;
    private String nickname;
    private String email;
    private String profileImage;
    private String introduction;
    private Integer age;
    private String gender;
    private boolean isVisible;


    public ProfileDTO checkNull(ProfileDTO profileDTO, Profile profile) {

        return ProfileDTO.builder()
                .nickname(profileDTO.nickname == null ? profile.getNickname() : profileDTO.nickname)
                .email(profileDTO.email == null ? profile.getEmail() : profileDTO.email)
                .profileImage(profileDTO.profileImage == null ? profile.getProfileImage() : profileDTO.profileImage)
                .introduction(profileDTO.introduction == null ? profile.getIntroduction() : profileDTO.introduction)
                .age(profileDTO.age == null ? profile.getAge() : profileDTO.age)
                .gender(profileDTO.gender == null ? profile.getGender() : profileDTO.gender)
                .build();
    }
}
