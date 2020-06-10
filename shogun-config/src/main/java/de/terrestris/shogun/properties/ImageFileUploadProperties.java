package de.terrestris.shogun.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "imagefile")
public class ImageFileUploadProperties {

    private List<String> supportedContentTypes;

    private Integer thumbnailSize;

}
