package de.terrestris.shogun.importer.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "importer")
@Data
public class ImporterProperties {

    @NestedConfigurationProperty
    private GeoServerProperties geoserver;

    @NestedConfigurationProperty
    private SHOGunProperties shogun;

    @NestedConfigurationProperty
    private RasterImportProperties raster;

    @NestedConfigurationProperty
    private VectorImportProperties vector;

    private String targetWorkspace;

    private String targetEPSG;

    private String interceptorEndpoint;

    private Integer httpTimeout;

}
