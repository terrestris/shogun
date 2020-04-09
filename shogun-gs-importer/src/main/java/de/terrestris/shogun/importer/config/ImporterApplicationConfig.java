package de.terrestris.shogun.importer.config;

import de.terrestris.shogun.importer.GeoServerRESTImporter;
import de.terrestris.shogun.importer.config.properties.GeoServerProperties;
import de.terrestris.shogun.importer.config.properties.ImporterProperties;
import de.terrestris.shogun.importer.config.properties.RasterImportProperties;
import de.terrestris.shogun.importer.config.properties.VectorImportProperties;
import de.terrestris.shogun.properties.KeycloakAuthProperties;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
@ComponentScan(basePackages = {"de.terrestris.shogun.importer", "de.terrestris.shogun.lib.util"})
@EnableConfigurationProperties({
    ImporterProperties.class,
    GeoServerProperties.class,
    RasterImportProperties.class,
    VectorImportProperties.class,
    KeycloakAuthProperties.class
})
public class ImporterApplicationConfig {

    @Autowired
    protected ImporterProperties importerProperties;

    @Bean
    public GeoServerRESTManager geoServerManager() throws URISyntaxException, MalformedURLException {
        return new GeoServerRESTManager(
            new URI(importerProperties.getGeoserver().getBaseUrl()).toURL(),
            importerProperties.getGeoserver().getUsername(),
            importerProperties.getGeoserver().getPassword()
        );
    }

    @Bean
    public GeoServerRESTImporter geoServerImporter() throws URISyntaxException {
        return new GeoServerRESTImporter(
            new URI(String.format("%s/rest/imports", importerProperties.getGeoserver().getBaseUrl())),
            importerProperties.getGeoserver().getUsername(),
            importerProperties.getGeoserver().getPassword()
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(ImporterApplicationConfig.class, args);
    }

}
