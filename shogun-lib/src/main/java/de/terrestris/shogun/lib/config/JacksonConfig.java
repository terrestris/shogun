package de.terrestris.shogun.lib.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vladmihalcea.hibernate.type.util.ObjectMapperSupplier;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JacksonConfig implements ObjectMapperSupplier {

    private static ObjectMapper mapper;

    private static boolean initialized = false;

    @Bean
    public ObjectMapper objectMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    @Value("${shogun.srid:4326}")
    private int srid;

    @Value("${shogun.coordinatePrecisionScale:10}")
    private int coordinatePrecisionScale;

    @Override
    public ObjectMapper get() {
        return objectMapper();
    }

    @PostConstruct
    public void init() {
        if (!initialized) {
            GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(coordinatePrecisionScale), srid);
            JacksonConfig.mapper.registerModule(new JtsModule(geomFactory));

            var javaTimeModule = new JavaTimeModule();
            JacksonConfig.mapper.registerModule(javaTimeModule);
            JacksonConfig.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

            JacksonConfig.mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
            JacksonConfig.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JacksonConfig.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            for (var entry : findAnnotatedClasses().entrySet()) {
                JacksonConfig.mapper.addMixIn(entry.getKey(), entry.getValue());
            }
        }
        initialized = true;
    }

    /**
     * Find classes having types annotated with @{@link JsonSuperType} using reflections
     * @return Map of matching classes
     */
    private Map<Class<?>, Class<?>> findAnnotatedClasses() {
        var reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forJavaClassPath())
            .setScanners(new SubTypesScanner(),
                new TypeAnnotationsScanner()));

        Map<Class<?>, Class<?>> implementers = new HashMap<>();

        // this finds the type furthest down along the implementation chain
        for (var cl : reflections.getTypesAnnotatedWith(JsonSuperType.class)) {
            var annotation = cl.getAnnotation(JsonSuperType.class);
            var superType = annotation.type();

            if (!annotation.override() && !superType.isInterface()) {
                throw new IllegalStateException("The super type " + superType.getName() + " is not an interface. " +
                    "Set override to true if this is intended.");
            }

            if (!implementers.containsKey(superType)) {
                implementers.put(superType, cl);
            } else {
                var previous = implementers.get(superType);
                if (previous.isAssignableFrom(cl)) {
                    implementers.put(superType, cl);
                } else if (!cl.isAssignableFrom(previous)) {
                    throw new IllegalStateException("Found 2 incompatible types that both want to deserialize to the type "
                        + superType.getName() + ". Any existing type should get extended.");
                }
            }
        }
        return implementers;
    }

}
