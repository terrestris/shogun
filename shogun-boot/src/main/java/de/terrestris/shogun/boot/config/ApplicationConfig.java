package de.terrestris.shogun.boot.config;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = { "de.terrestris.shogun.lib", "de.terrestris.shogun.boot" })
@ComponentScan(basePackages = { "de.terrestris.shogun.lib", "de.terrestris.shogun.boot" })
@EntityScan(basePackages = { "de.terrestris.shogun.lib", "de.terrestris.shogun.boot" })
public class ApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }

}
