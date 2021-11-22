package de.terrestris.shogun.boot.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Log4j2
@Component
public class SocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection {}", event);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("Disconnected web socket event {}", event);

//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
//
//        if (username != null) {
//            log.info("User Disconnected : " + username);
//
//            SimpleSocketMessage message = new SimpleSocketMessage();
//            message.setMessage("Leaved");
//
//            messagingTemplate.convertAndSend("/topic/public", message);
//        }
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        log.info("Received a web socket subscribe event {}", event);
    }

    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        log.info("Received a web socket unsubscribe event {}", event);
    }

}
