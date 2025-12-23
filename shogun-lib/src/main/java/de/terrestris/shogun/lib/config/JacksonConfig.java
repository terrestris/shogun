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

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import io.hypersistence.utils.hibernate.type.util.ObjectMapperSupplier;
import lombok.extern.log4j.Log4j2;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Configuration
// TODO Is the implementatio of ObjectMapperSupplier still needed with the custom mappers
//      in place? -> there should be no connection between jackson and hibernate anymore?
public class JacksonConfig implements ObjectMapperSupplier {

    private static ObjectMapper mapper;

    private static boolean initialized = false;

    @Value("${shogun.srid:4326}")
    private int srid;

    @Value("${shogun.coordinatePrecisionScale:10}")
    private int coordinatePrecisionScale;

    @Bean
    public ObjectMapper objectMapper() {
        log.info("Initializing custom ObjectMapper");
        if (mapper == null || !initialized) {
            log.info("Creating new ObjectMapper instance");

            GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(coordinatePrecisionScale), srid);

            JsonMapper.Builder builder = JsonMapper.builder()
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .addModule(new JtsModule(geomFactory));

            // Apply mixins after building to properly register @JsonDeserialize mappings
            Map<Class<?>, Class<?>> mixins = findAnnotatedClasses();
            for (Map.Entry<Class<?>, Class<?>> entry : mixins.entrySet()) {
                log.info("Registering mixin: {} -> {}", entry.getKey().getSimpleName(), entry.getValue().getSimpleName());
                builder.addMixIn(entry.getKey(), entry.getValue());
            }

            mapper = builder.build();

            initialized = true;
        }
        return mapper;
    }

    @Override
    public ObjectMapper get() {
        if (mapper == null || !initialized) {
            // For cases where ObjectMapperSupplier is called before Spring bean initialization
            return objectMapper();
        }
        return mapper;
    }

    /**
     * Find classes having types annotated with @{@link JsonSuperType} using reflections
     * @return Map of matching classes
     */
    private Map<Class<?>, Class<?>> findAnnotatedClasses() {
        var reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forJavaClassPath())
            .setScanners(
                Scanners.SubTypes,
                Scanners.TypesAnnotated
            )
        );

        Map<Class<?>, Class<?>> implementers = new HashMap<>();

        // this finds the type the furthest down along the implementation chain
        for (var cl : reflections.getTypesAnnotatedWith(JsonSuperType.class)) {
            var annotation = cl.getAnnotation(JsonSuperType.class);
            var superType = annotation.type();

            if (!implementers.containsKey(superType)) {
                implementers.put(superType, cl);
                log.debug("(De-)serializing supertype " + superType + " with type " + cl);
                continue;
            }

            var previous = implementers.get(superType);
            if (previous.isAssignableFrom(cl)) {
                implementers.put(superType, cl);
                log.debug("(De-)serializing supertype " + superType + " with type " + cl);
                continue;
            }

            var currentOverride = annotation.override();
            var previousOverride = previous.getAnnotation(JsonSuperType.class).override();

            log.warn("Found two conflicting (de-)serialization candidates (" + cl.getName() + ", " +
                previous.getName() + ") for supertype " + superType + ". Checking for a conflict resolution " +
                "(via the override field)");

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
                log.info("Existing type (" + previous.getName() + ") for (de-)serialization of " + superType.getName() +
                    " will be used as override is set to true");
                continue;
            }

            if (annotation.override()) {
                implementers.remove(previous);
                implementers.put(superType, cl);
                log.info("Removing the existing type for (de-)serialization of " + superType.getName() + " (" +
                    previous.getName() + ") in favour of " + cl.getName() + " as override is set to true");
            }
        }

        return implementers;
    }

}
