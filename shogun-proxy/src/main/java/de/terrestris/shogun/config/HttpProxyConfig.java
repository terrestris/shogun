package de.terrestris.shogun.config;

import de.terrestris.shogun.properties.KeycloakAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@EnableAutoConfiguration
@ComponentScan(
    basePackages = {"de.terrestris.shogun", "${scan.package:null}"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "de.terrestris.shogun.lib.*")
)
@EnableConfigurationProperties({
    KeycloakAuthProperties.class
})
public class HttpProxyConfig {

    public static void main(String[] args) {
        SpringApplication.run(HttpProxyConfig.class, args);
    }

}
