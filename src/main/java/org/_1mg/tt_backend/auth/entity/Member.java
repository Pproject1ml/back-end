package org._1mg.tt_backend.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.Role;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org._1mg.tt_backend.base.BaseEntity;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_id_for_login", columnNames = {"oauthId"}),

        @UniqueConstraint(name = "unique_nickname", columnNames = {"nickname"})})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID memberId;

    @Column(nullable = false)
    private String nickname;

    private String email;

    private String profileImage;

    private String introduction;

    private Integer age;

    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Column(updatable = false, nullable = false)
    private String oauthId;

    @Column(updatable = false, nullable = false)
    private String oauthProvider;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isVisible = false;

    private String refreshToken;

    public void updateDelete(boolean deleted) {
        this.isDeleted = deleted;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public MemberDTO convertToDTO() {

        return MemberDTO.builder()
                .memberId(this.memberId.toString())
                .nickname(this.getNickname())
                .email(this.getEmail()).profileImage(this.getProfileImage()).introduction(this.getIntroduction()).age(this.getAge()).gender(this.getGender()).role(this.getRole()).oauthId(this.getOauthId()).oauthProvider(this.getOauthProvider()).createdAt(this.getCreatedAt()).updatedAt(this.getUpdateAt()).isDeleted(this.getIsDeleted()).isVisible(this.getIsVisible()).build();
    }

    public void updateMember(MemberDTO dto) {

        this.nickname = dto.getNickname();
        this.profileImage = dto.getProfileImage();
        this.introduction = dto.getIntroduction();
        this.age = dto.getAge();
        this.gender = dto.getGender();
        this.isDeleted = dto.getIsDeleted();
        this.isVisible = dto.getIsVisible();
    }

    public void deleteRefreshToken() {
        this.refreshToken = null;
    }
}
