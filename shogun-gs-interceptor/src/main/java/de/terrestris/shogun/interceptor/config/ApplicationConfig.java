package de.terrestris.shogun.interceptor.config;

import de.terrestris.shogun.interceptor.config.properties.InterceptorProperties;
import de.terrestris.shogun.interceptor.config.properties.NamespaceProperties;
import de.terrestris.shogun.properties.KeycloakAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = { "de.terrestris.shogun.interceptor" })
@ComponentScan(basePackages = { "de.terrestris.shogun.interceptor" })
@EntityScan(basePackages = { "de.terrestris.shogun.interceptor" })
@EnableConfigurationProperties({
    InterceptorProperties.class,
    NamespaceProperties.class,
    KeycloakAuthProperties.class
})
public class ApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }

}
