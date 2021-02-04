package de.terrestris.shogun.lib.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JacksonConfig extends ObjectMapper {

    @Bean
    public ObjectMapper objectMapper() {
        init();
        return this;
    }

    private boolean isInitialized = false;

    @Value("${shogun.srid:4326}")
    protected int srid;

    @Value("${shogun.coordinatePrecisionScale:10}")
    protected int coordinatePrecisionScale;

    @Bean
    public JtsModule jtsModule() {
        GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(coordinatePrecisionScale), srid);
        return new JtsModule(geomFactory);
    }

    @PostConstruct
    public void init() {
        if (!isInitialized) {
            this.registerModule(jtsModule());

            this.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
            this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

            for (var entry : findAnnotatedClasses().entrySet()) {
                this.addMixIn(entry.getKey(), entry.getValue());
            }
            isInitialized = true;
        }
    }

    private static Map<Class<?>, Class<?>> findAnnotatedClasses() {
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

    @Override
    public <T> T readValue(String content, JavaType valueType) throws JsonProcessingException {
        if (!isInitialized) {
            this.init();
        }

        return super.readValue(content, valueType);
    }

}
