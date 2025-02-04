package org._1mg.tt_backend.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.dto.LoginDTO;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.auth.dto.SignupDTO;
import org._1mg.tt_backend.auth.entity.Member;
import org._1mg.tt_backend.auth.security.CustomUserDetails;
import org._1mg.tt_backend.auth.service.MemberService;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static org._1mg.tt_backend.base.CustomException.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "실제론 이용되지 않는 컨트롤러",
            description = "로그인 URL은 Filter에서 이루어짐 이건 오로지 프론트를 위한 명령 기능 완성되면 삭제해야 함"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 로그인 - body.status : 20"),
            @ApiResponse(responseCode = "401",
                    description = """
                            기본 인증 관련 예외(따로 코드가 존재하지 않는 예외) - body.status : 30
                            없는 회원 - body.status : 31\n
                            인증이 필요한 요청 - body.status : 32\n
                            인증은 됐지만 권한이 없는 요청 - body.status : 33\n
                            기본 토큰 관련 예외 - body.status : 50\n
                            토큰 만료 - body.status : 51\n
                            """
            )
    })
    @PostMapping("/auth/login")
    public ResponseDTO<MemberDTO> login(@RequestBody LoginDTO loginDTO) {

        return null;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "회원가입 성공 - body.status : 20"
            ),
            @ApiResponse(responseCode = "400",
                    description = "이미 존재하는 회원 - body.status : 41"
            )
    })
    @PostMapping("/auth/signup")
    public ResponseDTO<String> signup(@RequestBody SignupDTO signupDTO) {

        log.debug("SIGN UP START");

        ProfileDTO profile = ProfileDTO.builder()
                .nickname(signupDTO.getNickname())
                .email(signupDTO.getEmail())
                .profileImage(signupDTO.getProfileImage())
                .introduction(signupDTO.getIntroduction())
                .age(signupDTO.getAge())
                .gender(signupDTO.getGender())
                .isVisible(signupDTO.getIsVisible())
                .build();

        MemberDTO member = MemberDTO.builder()
                .profile(profile)
                .oauthId(signupDTO.getOauthId())
                .oauthProvider(signupDTO.getOauthProvider())
                .build();

        memberService.signup(member);

        log.debug("SIGN UP FINISHED");
        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message("SIGNUP SUCCESS")
                .build();
    }

    @Parameter(name = "memberId")
    @Operation(
            description = "Authorization 헤더가 포함되면 안됨"
    )
    @PostMapping("/auth/refresh")
    public ResponseEntity<ResponseDTO<String>> refresh(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON BODY",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ " + '\"' + "memberId" + '\"' + ":" + '\"' + "value" + '\"' + "}")
                    )
            ) @RequestBody Map<String, String> refreshDTO) {

        log.debug("TOKEN REFRESH START");
        String accessToken = memberService.refresh(refreshDTO.get("memberId"));

        log.debug("TOKEN REFRESH FINISHED");
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + accessToken)
                .body(ResponseDTO.<String>builder()
                        .status(OK.getStatus())
                        .message("REFRESH SUCCESS")
                        .build()
                );
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "고유한 닉네임 - body.status : 20\n"
            ),
            @ApiResponse(responseCode = "400",
                    description = "중복된 닉네임 - body.status : 42\n"
            )
    })
    @Parameter(name = "nickname")
    @PostMapping("/auth/check-nickname")
    public ResponseDTO<Map<String, String>> checkUniqueNickname(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON BODY",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ " + '\"' + "nickname" + '\"' + ":" + '\"' + "value" + '\"' + "}")
                    )
            ) @RequestBody Map<String, String> nicknameDTO) throws JsonProcessingException {

        log.debug("CHECK NICKNAME START");
        memberService.checkUniqueNickname(nicknameDTO.get("nickname"));

        log.debug("CHECK NICKNAME FINISHED");
        return ResponseDTO.<Map<String, String>>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(nicknameDTO)
                .build();
    }

    @GetMapping("/user")
    public ResponseDTO<MemberDTO> userinfo(Authentication authentication) {

        log.debug("GET USERINFO START");

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        Member member = memberService.findMemberNotDeleted(user.getMemberId());

        log.debug("GET USERINFO FINISHED");
        return ResponseDTO.<MemberDTO>builder()
                .status(OK.getStatus())
                .message("USER INFO")
                .data(member.convertToDTO())
                .build();
    }

    // 회원수정
    @PatchMapping(value = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<ProfileDTO> userinfo(@RequestPart ProfileDTO profile,
                                            @RequestPart(required = false) MultipartFile profileImage,
                                            Principal principal) throws IOException {
        log.debug("UPDATE USERINFO START");

        String memberId = principal.getName();
        ProfileDTO newProfile = memberService.updateMember(profile, profileImage, memberId);

        log.debug("UPDATE USERINFO FINISHED");

        return ResponseDTO.<ProfileDTO>builder()
                .status(OK.getStatus())
                .message("UPDATE USERINFO SUCCESS")
                .data(newProfile)
                .build();
    }

    @PostMapping("/logout")
    public ResponseDTO<String> logout(Authentication authentication) {

        log.info("LOGOUT START");
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        memberService.logout(user.getMemberId());

        SecurityContextHolder.getContext().setAuthentication(null);

        log.info("LOGOUT FINISHED");
        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }

    @DeleteMapping("/user")
    public ResponseDTO<String> deleteUser(Principal principal) {

        memberService.deleteMember(principal.getName());

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }

    @GetMapping("/auth/check-jwt")
    public ResponseDTO<MemberDTO> checkJwt(@RequestParam("jwt") String jwtToken) {

        log.info("start check jwt");
        String token = jwtToken.split(" ")[1];
        MemberDTO memberDTO = memberService.checkJwtToken(token);

        log.info("end check jwt");
        return ResponseDTO.<MemberDTO>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .data(memberDTO)
                .build();
    }

    @GetMapping("/auth/check-fcm")
    public ResponseDTO<String> checkFcm(@RequestParam("id") String profileId, @RequestParam("fcm") String fcmToken) {

        memberService.checkFcmToken(profileId, fcmToken);

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message(OK.getMessage())
                .build();
    }
}
