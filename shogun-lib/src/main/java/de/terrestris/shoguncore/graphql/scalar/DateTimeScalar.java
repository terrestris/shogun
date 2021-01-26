package de.terrestris.shoguncore.graphql.scalar;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shoguncore.config.JacksonConfig;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.GraphQLScalarType;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

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

        @SneakyThrows
        @Override
        public Object parseValue(Object input) {
            return parseDateFromVariable(input);
        }

        @SneakyThrows
        @Override
        public Object parseLiteral(Object input) {
            return parseDateFromAstLiteral(input);
        }
    });

    private static Object serializeDate(Object dataFetcherResult) {
        return objectMapper().convertValue(dataFetcherResult, String.class);
    }

    private static Object parseDateFromVariable(Object dataFetcherResult) {
        if (dataFetcherResult instanceof String) {
            String dateTimeString = (String)dataFetcherResult;
            try {
                return objectMapper().readValue(dateTimeString, Date.class);
            } catch (JsonProcessingException e) {
                throw new CoercingParseValueException("Unable to parse variable value " + dataFetcherResult + " as DateTime");
            }
        }
        throw new CoercingParseValueException("Unable to parse variable value " + dataFetcherResult + " as DateTime");
    }

    private static Object parseDateFromAstLiteral(Object dataFetcherResult) {
        if (dataFetcherResult instanceof StringValue) {
            String dateTimeString = ((StringValue) dataFetcherResult).getValue();;
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JtsModule());
            try {
                return mapper.readValue(dateTimeString, Date.class);
            } catch (JsonProcessingException e) {
                throw new CoercingParseValueException("Unable to parse value " + dataFetcherResult + " as DateTime");
            }
        }
        throw new CoercingParseLiteralException("Value is not DateTime");
    }
}
