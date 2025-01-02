package org._1mg.tt_backend.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupDTO {

    String nickname;
    String email;
    String profileImage;
    String introduction;
    Integer age;
    String gender;
    String oauthId;
    String oauthProvider;
}
