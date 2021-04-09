package de.terrestris.shogun.interceptor.config;

import de.terrestris.shogun.interceptor.config.properties.InterceptorProperties;
import de.terrestris.shogun.interceptor.config.properties.NamespaceProperties;
import de.terrestris.shogun.lib.envers.CustomEnversRevisionRepositoryFactoryBean;
import de.terrestris.shogun.properties.KeycloakAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
    basePackages = {"de.terrestris.shogun", "${scan.package:null}"},
    repositoryFactoryBeanClass = CustomEnversRevisionRepositoryFactoryBean.class
)
@ComponentScan(basePackages = {"de.terrestris.shogun", "${scan.package:null}"})
@EntityScan(basePackages = {"de.terrestris.shogun", "${scan.package:null}"})
@EnableConfigurationProperties({
    InterceptorProperties.class,
    NamespaceProperties.class,
    KeycloakAuthProperties.class
})
public class InterceptorApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(InterceptorApplicationConfig.class, args);
    }

}
