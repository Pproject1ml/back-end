package org._1mg.tt_backend.base;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {

    private int status;

    private String message;

    private T data;
}
