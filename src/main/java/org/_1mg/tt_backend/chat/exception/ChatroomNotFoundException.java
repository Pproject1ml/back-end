package org._1mg.tt_backend.chat.exception;

public class ChatroomNotFoundException extends RuntimeException {
    public ChatroomNotFoundException(String message) {
        super(message);
    }
}
