package de.terrestris.shoguncore.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Set;

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

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            for (var cl : findAnnotatedClasses()) {
                var type = cl.getAnnotation(JsonSuperType.class).type();
                mapper.addMixIn(type, cl);
            }
        }
    }

    private static Set<Class<?>> findAnnotatedClasses() {
        var reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forJavaClassPath())
            .setScanners(new SubTypesScanner(),
                new TypeAnnotationsScanner()));

        return reflections.getTypesAnnotatedWith(JsonSuperType.class);
    }

}