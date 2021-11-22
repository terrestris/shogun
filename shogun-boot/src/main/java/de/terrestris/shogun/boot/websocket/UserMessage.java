package de.terrestris.shogun.boot.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserMessage extends NotificationMessage {
    private String user;
}
