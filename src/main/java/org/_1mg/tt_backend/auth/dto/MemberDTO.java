package org._1mg.tt_backend.auth.dto;

import lombok.*;
import org._1mg.tt_backend.auth.entity.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {

    private String memberId;
    private ProfileDTO profile;
    private Role role;
    private String oauthId;
    private String oauthProvider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    public MemberDTO(MemberDTO memberDTO) {

        this.memberId = memberDTO.memberId;
        this.profile = memberDTO.profile;
        this.role = memberDTO.role;
        this.oauthId = memberDTO.oauthId;
        this.oauthProvider = memberDTO.oauthProvider;
        this.createdAt = memberDTO.createdAt;
        this.updatedAt = memberDTO.updatedAt;
        this.isDeleted = memberDTO.isDeleted;
    }
}
