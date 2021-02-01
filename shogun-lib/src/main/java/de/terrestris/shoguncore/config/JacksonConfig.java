package de.terrestris.shoguncore.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vladmihalcea.hibernate.type.util.ObjectMapperSupplier;
import de.terrestris.shoguncore.annotation.JsonSuperType;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

@Configuration
public class JacksonConfig implements ObjectMapperSupplier {

    private static ObjectMapper mapper;

    @Bean
    public ObjectMapper objectMapper() {
        init();
        return mapper;
    }

    @Bean
    public static JtsModule jtsModule() {
        GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(10), 4326);

        return new JtsModule(geomFactory);
    }

    @Override
    public ObjectMapper get() {
        return objectMapper();
    }

    @PostConstruct
    public static void init() {
        if (mapper == null) {
            mapper = new ObjectMapper().registerModule(jtsModule());

            mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

            for (var entry : findAnnotatedClasses().entrySet()) {
                mapper.addMixIn(entry.getKey(), entry.getValue());
            }
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

}
