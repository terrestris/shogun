package de.terrestris.shogun.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    @NestedConfigurationProperty
    private FileUploadProperties file;

    @NestedConfigurationProperty
    private ImageFileUploadProperties image;

}
