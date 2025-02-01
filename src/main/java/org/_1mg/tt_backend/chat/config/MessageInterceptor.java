package org._1mg.tt_backend.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageInterceptor implements ChannelInterceptor {

    //COMMAND가 SEND 메세지 수신 시 로그
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);


        log.info("========== STOMP SEND EVENT ==========");
        log.info("Command: {}", headerAccessor.getCommand());
        log.info("Session ID: {}", headerAccessor.getSessionId());
        //log.info("User: {}", headerAccessor.getUser().getName());
        log.info("Destination: {}", headerAccessor.getDestination());
        log.info("Payload: {}", new String((byte[]) message.getPayload()));
        log.info("message : {}", message);

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);


        log.info("========== STOMP SEND EVENT END ==========");
        log.info("Command: {}", headerAccessor.getCommand());
        log.info("Session ID: {}", headerAccessor.getSessionId());
        //log.info("User: {}", headerAccessor.getUser().getName());
        log.info("Destination: {}", headerAccessor.getDestination());
        log.info("Payload: {}", new String((byte[]) message.getPayload()));
        log.info("message : {}", message);
    }
}
