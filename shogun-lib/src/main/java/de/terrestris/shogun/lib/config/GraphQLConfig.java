package de.terrestris.shogun.lib.config;

import de.terrestris.shogun.lib.graphql.scalar.DateTimeScalar;
import de.terrestris.shogun.lib.graphql.scalar.GeometryScalar;
import de.terrestris.shogun.lib.graphql.scalar.JsonScalar;
import de.terrestris.shogun.lib.graphql.scalar.LongScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> {
            wiringBuilder.scalar(JsonScalar.INSTANCE);
            wiringBuilder.scalar(GeometryScalar.INSTANCE);
            wiringBuilder.scalar(DateTimeScalar.INSTANCE);
            wiringBuilder.scalar(LongScalar.INSTANCE);
        };
    }

}
