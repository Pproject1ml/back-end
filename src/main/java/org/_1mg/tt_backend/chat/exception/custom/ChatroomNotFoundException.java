package org._1mg.tt_backend.chat.exception.custom;

public class ChatroomNotFoundException extends RuntimeException {
    public ChatroomNotFoundException(String message) {
        super(message);
    }
}
