package de.terrestris.shogun.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
public abstract class SwaggerConfig {

    protected String title = "SHOGun REST API";
    protected String description = "This is the REST API description of SHOGun";
    protected String version = "1.0.0";
    protected String termsOfServiceUrl = "https://www.terrestris.de/en/impressum/";
    protected Contact contact = new Contact("terrestris GmbH & Co. KG", "www.terrestris.de", "info@terrestris.de");
    protected String license = "MIT";
    protected String licenseUrl = "https://opensource.org/licenses/MIT";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo());
    }

    protected ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
            title,
            description,
            version,
            termsOfServiceUrl,
            contact,
            license,
            licenseUrl,
            Collections.emptyList()
        );

        return apiInfo;
    }
}
