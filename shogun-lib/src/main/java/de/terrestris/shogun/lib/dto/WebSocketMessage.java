package de.terrestris.shogun.lib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebSocketMessage {

    public WebSocketMessage(String message) {
        this.message = message;
    }

    private String message;

    private Map<String, Object> data = Collections.emptyMap();

}
