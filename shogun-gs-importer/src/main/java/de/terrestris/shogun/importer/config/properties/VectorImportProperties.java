package de.terrestris.shogun.importer.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "vector")
@Data
public class VectorImportProperties {

    private String targetDatastore;

    private Map<String, String> targetDatastoreConnectionParams;

}
