package org._1mg.tt_backend.base;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {

    //응답에 대한 상태 코드 - 개발자가 만든 상태 코드
    //exception.CustomException enum 파일에 추가 혹은 있는걸 사용하면 됨
    private int status;

    //응답에 대한 간단한 설명
    //exception.handler.* 내부 클래스 참고
    //- 예시 NEED TO SIGN IN 
    private String message;

    //요청에 대한 응답 결과 데이터 제네릭 타입으로 직접 넣어주면 됨
    //Member Controller 참고
    //예시
    //ResponseDTO<String> - 필요한 데이터가 없을 때 타입으로 뭐라도 넣어야 해서 String으로 넣음
    //ResponseDTO<MemberDTO> - MemberDTO의 데이터가 응답 데이터로 활용될 때
    private T data;
}
