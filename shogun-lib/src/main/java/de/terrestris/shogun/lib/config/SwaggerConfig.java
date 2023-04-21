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
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
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
                    .termsOfService(termsOfServiceUrl)
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


    /**
     * To accept an arbitrary free-form object as value, a class field can be of type "HashMap<String, Object>".
     * Whereas this is correct for persistence and internal handling, the OpenAPI specification (generated by SpringDoc)
     * will apply the type "object" as additional property for the given field and thus client any validation might
     * result in false negatives e.g. if the given value is of type "string". Generally it's possible to allow for
     * arbitrary values by setting the "additionalProperties" property to "{}" or "true" to overcome this
     * (see https://swagger.io/docs/specification/data-models/dictionaries/).
     *
     * Unfortunately the "additionalProperties" property of the "Schema" annotation can't be used as it seems not to be
     * supported by SpringDoc right now (see https://github.com/springdoc/springdoc-openapi/issues/1927).
     *
     * This method tries to find all "HashMap<String, Object>" occurrences in the generated specification and
     * updates the "additionalProperties" property to "true".
     *
     * Example:
     *
     * The generated schema definition of
     *
     * {
     *   "additionalProperties": {
     *     "description": "The configuration object of the tool.",
     *     "example": {
     *       "visible": true
     *     },
     *     "type": "object"
     *   },
     *   "description": "The configuration object of the tool.",
     *   "example": {
     *     "visible": true
     *   },
     *   "type": "object"
     * }
     *
     * will be transformed to
     *
     * {
     *   "additionalProperties": true,
     *   "description": "The configuration object of the tool.",
     *   "example": {
     *     "visible": true
     *   },
     *   "type": "object"
     * }
     */
    @Bean
    public OpenApiCustomiser enableArbitraryObjects() {
        return openApi -> openApi.getComponents().getSchemas().values().forEach( s -> enableArbitraryObjects(s));
    }

    private void enableArbitraryObjects(Schema schema) {
        if (schema instanceof MapSchema) {
            if (schema.getAdditionalProperties() instanceof Schema &&
                ((Schema) schema.getAdditionalProperties()).getType().equalsIgnoreCase("object")) {
                schema.setAdditionalProperties(true);
            }
        } else if (schema.getType() != null && schema.getType().equalsIgnoreCase("object") &&
            schema.getProperties() != null) {
            Map<String, Schema> properties = schema.getProperties();
            properties.values().forEach(s -> enableArbitraryObjects(s));
        }
    }
}
