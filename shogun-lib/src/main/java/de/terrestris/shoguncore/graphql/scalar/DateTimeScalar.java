package de.terrestris.shoguncore.graphql.scalar;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shoguncore.config.JacksonConfig;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DateTimeScalar {
    protected static ObjectMapper _objectMapper;
    protected static ObjectMapper objectMapper() {
        if (_objectMapper == null) {
            _objectMapper = (new JacksonConfig()).objectMapper();
        }
        return _objectMapper;
    }

    public static final GraphQLScalarType DATE_TIME = new GraphQLScalarType("DateTime", "A custom scalar that handles date formats", new Coercing() {
        @Override
        public Object serialize(Object dataFetcherResult) {
            return serializeDate(dataFetcherResult);
        }

        @Override
        public Object parseValue(Object input) {
            return parseDateFromVariable(input);
        }

        @Override
        public Object parseLiteral(Object input) {
            return parseDateFromAstLiteral(input);
        }
    });

    private static Object serializeDate(Object dataFetcherResult) {
        return objectMapper().convertValue(dataFetcherResult, String.class);
    }

    private static Object parseDateFromVariable(Object dataFetcherResult) {
        return objectMapper().convertValue(dataFetcherResult, String.class);
    }

    private static Object parseDateFromAstLiteral(Object dataFetcherResult) {
        return objectMapper().convertValue(dataFetcherResult, String.class);
    }
}
