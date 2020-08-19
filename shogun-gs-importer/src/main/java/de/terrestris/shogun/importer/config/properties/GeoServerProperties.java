package de.terrestris.shogun.importer.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "geoserver")
@Data
public class GeoServerProperties {

    private String baseUrl;

    private String username;

    private String password;

}
