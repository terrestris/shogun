package de.terrestris.shogun.interceptor.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration()
@ConfigurationProperties(prefix = "namespaces")
@Data
public class NamespaceProperties {

    private String namespace;

    private String url;
}
