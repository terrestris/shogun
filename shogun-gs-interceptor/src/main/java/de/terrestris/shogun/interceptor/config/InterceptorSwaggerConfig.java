package de.terrestris.shogun.interceptor.config;

import de.terrestris.shogun.config.SwaggerConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;

import java.util.Collections;
import java.util.function.Predicate;

@Configuration
@EnableAutoConfiguration
public class InterceptorSwaggerConfig extends SwaggerConfig {

    @Override
    protected ApiInfo apiInfo() {
        return new ApiInfo(
            "SHOGun GeoServer Interceptor REST-API",
            description,
            version,
            termsOfServiceUrl,
            contact,
            license,
            licenseUrl,
            Collections.emptyList()
        );
    }

    @Override
    protected Predicate<String> setSecurityContextPaths() {
        return PathSelectors.any();
    }
}
