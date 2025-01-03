package org._1mg.tt_backend.auth.dto;

import lombok.*;
import org._1mg.tt_backend.auth.Role;
import org._1mg.tt_backend.auth.entity.Member;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {

    private String memberId;
    private String nickname;
    private String email;
    private String profileImage;
    private String introduction;
    private Integer age;
    private String gender;
    private Role role;
    private String oauthId;
    private String oauthProvider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private Boolean isVisible;

    public MemberDTO(MemberDTO memberDTO) {

        this.memberId = memberDTO.memberId;
        this.nickname = memberDTO.nickname;
        this.email = memberDTO.email;
        this.profileImage = memberDTO.profileImage;
        this.introduction = memberDTO.introduction;
        this.age = memberDTO.age;
        this.gender = memberDTO.gender;
        this.role = memberDTO.role;
        this.oauthId = memberDTO.oauthId;
        this.oauthProvider = memberDTO.oauthProvider;
        this.createdAt = memberDTO.createdAt;
        this.updatedAt = memberDTO.updatedAt;
        this.isDeleted = memberDTO.isDeleted;
        this.isVisible = memberDTO.isVisible;
    }

    public MemberDTO checkNull(MemberDTO memberDTO, Member member) {

        return MemberDTO.builder()
                .memberId(memberDTO.memberId == null ? member.getMemberId().toString() : memberDTO.memberId)
                .nickname(memberDTO.nickname == null ? member.getNickname() : memberDTO.nickname)
                .email(memberDTO.email == null ? member.getEmail() : memberDTO.email)
                .profileImage(memberDTO.profileImage == null ? member.getProfileImage() : memberDTO.profileImage)
                .introduction(memberDTO.introduction == null ? member.getIntroduction() : memberDTO.introduction)
                .age(memberDTO.age == null ? member.getAge() : memberDTO.age)
                .gender(memberDTO.gender == null ? member.getGender() : memberDTO.gender)
                .role(memberDTO.role == null ? member.getRole() : memberDTO.role)
                .oauthId(memberDTO.oauthId == null ? member.getOauthId() : memberDTO.oauthId)
                .oauthProvider(memberDTO.oauthProvider == null ? member.getOauthProvider() : memberDTO.oauthProvider)
                .createdAt(memberDTO.createdAt == null ? member.getCreatedAt() : memberDTO.createdAt)
                .updatedAt(memberDTO.updatedAt == null ? member.getUpdatedAt() : memberDTO.updatedAt)
                .isDeleted(memberDTO.isDeleted == null ? member.getIsDeleted() : memberDTO.isDeleted)
                .isVisible(memberDTO.isVisible == null ? member.getIsVisible() : memberDTO.isVisible)
                .build();
    }
}
