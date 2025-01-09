package org._1mg.tt_backend.exception.chat;

public class ChatroomNotFoundException extends RuntimeException {
    public ChatroomNotFoundException(String message) {
        super(message);
    }
}
