package de.terrestris.shoguncore.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public JtsModule jtsModule() {
        GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(10), 4326);

        return new JtsModule(geomFactory);
    }

}
