package org._1mg.tt_backend.chat.exception.custom;

public class AlreadyInChatroomException extends RuntimeException {
    public AlreadyInChatroomException(String message) {
        super(message);
    }
}
