package de.terrestris.shogun.lib.graphql.scalar;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shogun.lib.config.JacksonConfig;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import java.util.HashMap;
import lombok.extern.log4j.Log4j2;
import org.locationtech.jts.geom.Geometry;

@Log4j2
public class GeometryScalar {

    protected static ObjectMapper _objectMapper;

    protected static ObjectMapper objectMapper() {
        if (_objectMapper == null) {
            _objectMapper = (new JacksonConfig()).objectMapper();
        }
        return _objectMapper;
    }

    public static final GraphQLScalarType GEOMETRY = new GraphQLScalarType("Geometry", "A custom scalar that handles geometries", new Coercing() {

        @Override
        public Object serialize(Object dataFetcherResult) {
            return serializeGeometry(dataFetcherResult);
        }

        @Override
        public Object parseValue(Object input) {
            return parseGeometryFromVariable(input);
        }

        @Override
        public Object parseLiteral(Object input) {
            return parseGeometryFromAstLiteral(input);
        }
    });

    private static boolean isAGeometry(Object possibleGeometryValue) {
        return possibleGeometryValue instanceof Geometry;
    }

    private static Object serializeGeometry(Object dataFetcherResult) {
        if (isAGeometry(dataFetcherResult)) {
            Geometry geometry = (Geometry) dataFetcherResult;
            try {
                return objectMapper().readValue(objectMapper().writeValueAsString(geometry), HashMap.class);
            } catch (JsonProcessingException e) {
                log.error("JSON Processing error while writing geometry for GraphQL");
                log.trace("Full stack trace: ", e);
                throw new CoercingSerializeException(e.getMessage());
            }
        } else {
            throw new CoercingSerializeException("Unable to serialize " + dataFetcherResult + " as a geometry");
        }
    }

    private static Object parseGeometryFromVariable(Object input) {
        if (input instanceof String) {
            String geometryString = (String)input;
            try {
                return objectMapper().readValue(geometryString, HashMap.class);
            } catch (JsonProcessingException e) {
                throw new CoercingParseValueException("Unable to parse variable value " + input + " as a geometry");
            }
        }
        throw new CoercingParseValueException("Unable to parse variable value " + input + " as a geometry");
    }

    private static Object parseGeometryFromAstLiteral(Object input) {
        if (input instanceof StringValue) {
            String geometryString = ((StringValue) input).getValue();;
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JtsModule());
            try {
                return mapper.readValue(geometryString, HashMap.class);
            } catch (JsonProcessingException e) {
                throw new CoercingParseValueException("Unable to parse value " + input + " as a geometry");
            }
        }
        throw new CoercingParseLiteralException(
            "Value is not an geometry"
        );
    }
}
