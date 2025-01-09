package org._1mg.tt_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org._1mg.tt_backend.auth.entity.Gender;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {

    String nickname;
    String email;
    String profileImage;
    String introduction;
    Integer age;
    Gender gender;
    String oauthId;
    String oauthProvider;
    Boolean isVisible;
}
