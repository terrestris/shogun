package de.terrestris.shogun.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.function.Predicate;

@Configuration
@EnableAutoConfiguration
public abstract class SwaggerConfig {

    protected String title = "SHOGun REST API";
    protected String description = "This is the REST API description of SHOGun";
    protected String version = "1.0.0";
    protected String termsOfServiceUrl = "https://www.terrestris.de/en/impressum/";
    protected Contact contact = new Contact("terrestris GmbH & Co. KG", "www.terrestris.de", "info@terrestris.de");
    protected String license = "Apache License, Version 2.0";
    protected String licenseUrl = "https://opensource.org/licenses/Apache-2.0";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())
            .securityContexts(Collections.singletonList(actuatorSecurityContext()))
            .securitySchemes(Collections.singletonList(basicAuthScheme()));
    }

    private SecurityContext actuatorSecurityContext() {
        return SecurityContext.builder()
            .securityReferences(Collections.singletonList(basicAuthReference()))
            .forPaths(setSecurityContextPaths()::test)
            .build();
    }

    private SecurityReference basicAuthReference() {
        return new SecurityReference("basicAuth", new AuthorizationScope[0]);
    }

    private SecurityScheme basicAuthScheme() {
        return new BasicAuth("basicAuth");
    }

    protected ApiInfo apiInfo() {
        return new ApiInfo(
            title,
            description,
            version,
            termsOfServiceUrl,
            contact,
            license,
            licenseUrl,
            Collections.emptyList()
        );
    }

    /**
     * Define the project specific paths that require BasicAuth authentication.
     *
     * Possible return values could be:
     * <ul>
     *     <li>{@link PathSelectors#none()}</li>
     *     <li>{@link PathSelectors#any()}</li>
     *     <li>{@link PathSelectors#regex(String)}</li>
     *     <li>{@link PathSelectors#ant(String)}</li>
     * </ul>
     *
     *  Some examples for specifying a custom list of endpoints:
     *  <pre>
     *  PathSelectors.ant("/files/**");
     *  </pre>
     *
     *  <pre>
     *  Predicates.or(
     *      PathSelectors.ant("/files/**"),
     *      PathSelectors.ant("/applications/**")
     *  );
     *  </pre>
     *
     *  The latter requires {@code com.google.guava} on the classpath.
     *
     * @return The predicate that defines the secured paths.
     */
    protected abstract Predicate<String> setSecurityContextPaths();
}
