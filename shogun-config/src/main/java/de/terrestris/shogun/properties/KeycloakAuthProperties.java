package de.terrestris.shogun.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloakauth")
@Component
public class KeycloakAuthProperties {

    private String username;
    private String password;
    private String masterRealm;
    private String adminClient;

}
