package de.terrestris.shogun.boot.config;

import de.terrestris.shogun.lib.envers.CustomEnversRevisionRepositoryFactoryBean;
import de.terrestris.shogun.properties.FileUploadProperties;
import de.terrestris.shogun.properties.ImageFileUploadProperties;
import de.terrestris.shogun.properties.UploadProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
    basePackages = { "de.terrestris.shogun" },
    repositoryFactoryBeanClass = CustomEnversRevisionRepositoryFactoryBean.class
)
@ComponentScan(basePackages = { "de.terrestris.shogun" })
@EntityScan(basePackages = { "de.terrestris.shogun" })
@EnableConfigurationProperties({
    UploadProperties.class,
    FileUploadProperties.class,
    ImageFileUploadProperties.class
})
public class ApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }

}
