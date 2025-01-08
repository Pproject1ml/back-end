package org._1mg.tt_backend.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.entity.MemberChatEntity;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(nullable = false)
    private String nickname;

    private String email;

    private String profileImage;

    private String introduction;

    private Integer age;

    private String gender;

    @Column(nullable = false)
    private boolean isVisible;

    @OneToMany(mappedBy = "profile")
    private List<MemberChatEntity> chatrooms;

    public void updateProfile(ProfileDTO dto) {

        this.nickname = dto.getNickname();
        this.email = dto.getEmail();
        this.profileImage = dto.getProfileImage();
        this.introduction = dto.getIntroduction();
        this.age = dto.getAge();
        this.gender = dto.getGender();
        this.isVisible = dto.getIsVisible();
    }

    public ProfileDTO convertToDTO() {

        return ProfileDTO.builder()
                .profileId(this.profileId.toString())
                .nickname(this.nickname)
                .email(this.email)
                .profileImage(this.profileImage)
                .introduction(this.introduction)
                .age(this.age)
                .gender(this.gender)
                .isVisible(this.isVisible)
                .build();
    }
}
