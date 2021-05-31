package de.terrestris.shogun.lib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakEventDto {

    private String type;
    private String realmId;
    private String clientId;
    private String userId;
    private String ipAddress;
    private String resourcePath;
    private String resourceType;
    private Map<String, Object> details;

}
