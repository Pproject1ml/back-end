package org._1mg.tt_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    private String oauthId;

    private String email;

    private String oauthProvider;

    private String nickname;

    private String profileImage;
}
