package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.dto.WebSocketMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
public class WebSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String DEFAULT_DESTINATION = "/topic/events";

    WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendMessage(WebSocketMessage message) {
        simpMessagingTemplate.convertAndSend(DEFAULT_DESTINATION, message);
    }

    public void sendMessage(WebSocketMessage message, String destination) {
        simpMessagingTemplate.convertAndSend(destination, message);
    }
}
