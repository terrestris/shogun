/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.config;

import de.terrestris.shogun.lib.annotation.JsonSuperType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springdoc.core.SpringDocUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@Profile(value = {"!test"})
public abstract class SwaggerConfig {

    protected String title = "SHOGun REST API";
    protected String description = "This is the REST API description of SHOGun";
    protected String version = "1.0.0";
    protected String termsOfServiceUrl = "https://www.terrestris.de/en/impressum/";
    protected String contactName = "terrestris GmbH & Co. KG";
    protected String contactUrl = "www.terrestris.de";
    protected String contactMail = "info@terrestris.de";
    protected String license = "Apache License, Version 2.0";
    protected String licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt";

    @Bean
    protected OpenAPI apiInfo() {
        OpenAPI api = new OpenAPI()
            .info(
                new Info()
                    .title(title)
                    .description(description)
                    .version(version)
                    .contact(
                        new Contact()
                            .name(contactName)
                            .url(contactUrl)
                            .email(contactMail)
                    )
                    .license(
                        new License()
                            .name(license)
                            .url(licenseUrl)
                    )
            )
            .components(
                // TODO Make list of endpoints configurable?
                new Components()
                    .addSecuritySchemes(
                        "bearer-key",
                        new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            );

        replaceJsonSuperTypes();

        return api;
    }

    protected void replaceJsonSuperTypes() {
        var reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forJavaClassPath())
            .setScanners(
                Scanners.SubTypes,
                Scanners.TypesAnnotated
            )
        );

        Map<Class<?>, Class<?>> substitutions = new HashMap<>();

        for (var cl : reflections.getTypesAnnotatedWith(JsonSuperType.class)) {
            var annotation = cl.getAnnotation(JsonSuperType.class);
            var superType = annotation.type();

            if (!substitutions.containsKey(superType)) {
                substitutions.put(superType, cl);
                continue;
            }

            var previous = substitutions.get(superType);

            var currentOverride = annotation.override();
            var previousOverride = previous.getAnnotation(JsonSuperType.class).override();

            if (currentOverride && previousOverride) {
                throw new IllegalStateException("Found two types (" + cl.getName() + ", " + previous.getName() + ") " +
                    "that both want to (de-)serialize to the type " + superType.getName() + " and both have set " +
                    "override to true. Override must be set for a single type only.");
            }

            if (!currentOverride && !previousOverride) {
                throw new IllegalStateException("Found two types (" + cl.getName() + ", " + previous.getName() + ") " +
                    "that both want to (de-)serialize to the type " + superType.getName() + ". Any existing type " +
                    "should get extended.");
            }

            if (previousOverride) {
                continue;
            }

            if (annotation.override()) {
                substitutions.remove(previous);
                substitutions.put(superType, cl);
            }
        }

        for (var entry : substitutions.entrySet()) {
            Class<?> superType = entry.getKey();
            Class<?> cl = entry.getValue();

            SpringDocUtils.getConfig().replaceWithClass(superType, cl);
        }
    }
}
