package de.terrestris.shogun.boot.config;

import de.terrestris.shogun.config.SwaggerConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;

import java.util.Collections;

@Configuration
@EnableAutoConfiguration
public class BootSwaggerConfig extends SwaggerConfig {

    @Override
    protected ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
            "SHOGun Boot REST-API",
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
