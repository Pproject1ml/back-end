package org._1mg.tt_backend.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.base.BaseEntity;
import org._1mg.tt_backend.chat.entity.MemberChatEntity;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_id_for_login", columnNames = {"oauthId"}),
        @UniqueConstraint(name = "unique_nickname", columnNames = {"nickname"})
})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Column(updatable = false, nullable = false)
    private String oauthId;

    @Column(updatable = false, nullable = false)
    private String oauthProvider;

    private String refreshToken;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @OneToMany(mappedBy = "member")
    private List<MemberChatEntity> memberChatEntities;


    public void updateDelete(boolean deleted) {
        super.updateDeleted(deleted);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void deleteRefreshToken() {
        this.refreshToken = null;
    }

    public void updateMember(MemberDTO member) {

        this.oauthId = member.getOauthId();
        this.oauthProvider = member.getOauthProvider();
        this.refreshToken = getRefreshToken();
        this.profile = createProfile(member.getProfile());
        super.updateDeleted(member.isDeleted());
    }

    public static Member createMember(MemberDTO member) {

        return Member.builder()
                .oauthId(member.getOauthId())
                .oauthProvider(member.getOauthProvider())
                .profile(createProfile(member.getProfile()))
                .build();
    }

    public static Profile createProfile(ProfileDTO profileDTO) {

        return Profile.builder()
                .nickname(profileDTO.getNickname())
                .email(profileDTO.getEmail())
                .profileImage(profileDTO.getProfileImage())
                .introduction(profileDTO.getIntroduction())
                .age(profileDTO.getAge())
                .gender(profileDTO.getGender())
                .isVisible(profileDTO.isVisible())
                .build();
    }

    public MemberDTO convertToDTO() {

        return MemberDTO.builder()
                .memberId(this.memberId.toString())
                .role(this.role)
                .oauthId(this.oauthId)
                .oauthProvider(this.oauthProvider)
                .profile(this.profile.convertToDTO())
                .build();
    }

    public void updateProfile(ProfileDTO profileDTO) {

        profile.updateProfile(profileDTO.checkNull(profileDTO, profile));
    }
}
