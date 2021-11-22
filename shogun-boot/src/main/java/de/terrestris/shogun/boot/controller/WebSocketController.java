package de.terrestris.shogun.boot.controller;

import de.terrestris.shogun.boot.websocket.NotificationMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
public class WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/public")
    public NotificationMessage sendMessage(NotificationMessage message) {
        return message;
    }

}
