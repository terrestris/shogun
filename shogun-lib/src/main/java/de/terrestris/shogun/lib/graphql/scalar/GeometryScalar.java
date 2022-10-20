package de.terrestris.shogun.lib.graphql.scalar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.Internal;
import graphql.language.StringValue;
import graphql.schema.*;
import lombok.extern.log4j.Log4j2;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;

@Log4j2
@Internal
public class GeometryScalar {
    private GeometryScalar() {}

    static ObjectMapper om = new ObjectMapper();

    static final Coercing<Object, Object> GEOMETRY_COERCING = new Coercing<>() {
        @Override
        public Object serialize(Object dataFetcherResult) {
            if (isAGeometry(dataFetcherResult)) {
                Geometry geometry = (Geometry) dataFetcherResult;
                try {
                    return om.readValue(om.writeValueAsString(geometry), HashMap.class);
                } catch (JsonProcessingException e) {
                    log.error("JSON Processing error while writing geometry for GraphQL");
                    log.trace("Full stack trace: ", e);
                    throw new CoercingSerializeException(e.getMessage());
                }
            } else {
                throw new CoercingSerializeException("Unable to serialize " + dataFetcherResult + " as a geometry");
            }
        }

        @Override
        public Object parseValue(Object input) {
            if (input instanceof String) {
                String geometryString = (String)input;
                try {
                    return om.readValue(geometryString, HashMap.class);
                } catch (JsonProcessingException e) {
                    throw new CoercingParseValueException("Unable to parse variable value " + input + " as a geometry");
                }
            }
            throw new CoercingParseValueException("Unable to parse variable value " + input + " as a geometry");
        }

        @Override
        public Object parseLiteral(Object input) {
            if (input instanceof StringValue) {
                String geometryString = ((StringValue) input).getValue();;
                try {
                    return om.readValue(geometryString, HashMap.class);
                } catch (JsonProcessingException e) {
                    throw new CoercingParseValueException("Unable to parse value " + input + " as a geometry");
                }
            }
            throw new CoercingParseLiteralException(
                "Value is not an geometry"
            );
        }
    };

    static boolean isAGeometry(Object possibleGeometryValue) {
        return possibleGeometryValue instanceof Geometry;
    }

    public static final GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
        .name("Geometry")
        .description("A custom scalar that handles geometries")
        .coercing(GEOMETRY_COERCING)
        .build();
}
