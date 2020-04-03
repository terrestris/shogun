package de.terrestris.shogun.lib.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Value("${shogun.srid:4326}")
    protected int srid;

    @Value("${shogun.coordinatePrecisionScale:10}")
    protected int coordinatePrecisionScale;

    @Bean
    public JtsModule jtsModule() {
        GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(coordinatePrecisionScale), srid);
        return new JtsModule(geomFactory);
    }

}
