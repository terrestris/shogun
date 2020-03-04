package de.terrestris.shoguncore.graphql.scalar;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.StringValue;
import graphql.schema.*;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;

public class GeometryScalar {

    public static final GraphQLScalarType GEOMETRY = new GraphQLScalarType("Geometry", "A custom scalar that handles geometries", new Coercing() {
        @Override
        public Object serialize(Object dataFetcherResult) {
            return serializeGeometry(dataFetcherResult);
        }

        @Override
        public Object parseValue(Object input) {
            return parseGeoemtryFromVariable(input);
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
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JtsModule());
            try {
                return mapper.readValue(mapper.writeValueAsString(geometry), HashMap.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            throw new CoercingSerializeException("Unable to serialize " + dataFetcherResult + " as a geometry");
        }
        return null;
    }

    private static Object parseGeoemtryFromVariable(Object input) {
        if (input instanceof String) {
            String geometryString = (String)input;
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JtsModule());
            try {
                return mapper.readValue(geometryString, HashMap.class);
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
