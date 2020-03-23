package de.terrestris.shogun.interceptor.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration()
@ConfigurationProperties(prefix = "interceptor")
@Data
public class InterceptorProperties {

    private boolean namespaceBoundUrl;

    private String defaultOwsUrl;

    @NestedConfigurationProperty
    private List<NamespaceProperties> namespaces;
}
