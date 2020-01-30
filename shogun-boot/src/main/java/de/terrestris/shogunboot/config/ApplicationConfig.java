package de.terrestris.shogunboot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManagerFactory;

@SpringBootApplication
@EnableJpaRepositories(basePackages = { "de.terrestris.shoguncore", "de.terrestris.shogunboot" })
@ComponentScan(basePackages = { "de.terrestris.shoguncore", "de.terrestris.shogunboot" })
@EntityScan(basePackages = { "de.terrestris.shogunboot", "de.terrestris.shoguncore" })
public class ApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }

}
