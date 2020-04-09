package de.terrestris.shogun.importer.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "raster")
@Data
public class RasterImportProperties {

    private Boolean performGdalAddo;

    private List<Integer> gdalAddoLevels;

    private Boolean performGdalWarp;

}
