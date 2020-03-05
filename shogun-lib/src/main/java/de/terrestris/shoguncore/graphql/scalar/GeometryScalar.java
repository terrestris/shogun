package de.terrestris.shoguncore.graphql.scalar;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.StringValue;
import graphql.schema.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;

public class GeometryScalar {

    protected static ObjectMapper objectMapper = (new ObjectMapper()).registerModule(new JtsModule());

    protected final static Logger LOG = LogManager.getLogger(GeometryScalar.class);

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
                return objectMapper.readValue(objectMapper.writeValueAsString(geometry), HashMap.class);
            } catch (JsonProcessingException e) {
                LOG.error("JSON Processing error while writing geometry for GraphQL");
                LOG.trace("Full stack trace: ", e);
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
                return objectMapper.readValue(geometryString, HashMap.class);
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
