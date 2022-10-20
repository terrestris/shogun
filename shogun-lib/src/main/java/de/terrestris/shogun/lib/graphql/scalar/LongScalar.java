package de.terrestris.shogun.lib.graphql.scalar;

import graphql.Internal;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

@Log4j2
@Internal
public class LongScalar {

    private LongScalar() {}

    static final Coercing<Object, Object> OBJECT_COERCING = new Coercing<>() {
        private Long convertImpl(Object input) {
            if (input instanceof Long) {
                return (Long) input;
            } else if (isNumberIsh(input)) {
                BigDecimal value;
                try {
                    value = new BigDecimal(input.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
                try {
                    return value.longValueExact();
                } catch (ArithmeticException e) {
                    return null;
                }
            } else {
                return null;
            }

        }

        @Override
        public Long serialize(Object input) {
            Long result = convertImpl(input);
            if (result == null) {
                throw new CoercingSerializeException(
                    "Expected type 'Long' but was '" + typeName(input) + "'."
                );
            }
            return result;
        }

        @Override
        public Long parseValue(Object input) {
            Long result = convertImpl(input);
            if (result == null) {
                throw new CoercingParseValueException(
                    "Expected type 'Long' but was '" + typeName(input) + "'."
                );
            }
            return result;
        }

        @Override
        public Long parseLiteral(Object input) {
            if (input instanceof StringValue) {
                try {
                    return Long.parseLong(((StringValue) input).getValue());
                } catch (NumberFormatException e) {
                    throw new CoercingParseLiteralException(
                        "Expected value to be a Long but it was '" + input + "'"
                    );
                }
            } else if (input instanceof IntValue) {
                BigInteger value = ((IntValue) input).getValue();
                if (value.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0 || value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                    throw new CoercingParseLiteralException(
                        "Expected value to be in the Long range but it was '" + value + "'"
                    );
                }
                return value.longValue();
            }
            throw new CoercingParseLiteralException(
                "Expected AST type 'IntValue' or 'StringValue' but was '" + typeName(input) + "'."
            );
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Long result = Objects.requireNonNull(convertImpl(input));
            return IntValue.newIntValue(BigInteger.valueOf(result)).build();
        }
    };

    public static GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
        .name("Long")
        .description("A 64-bit signed integer")
        .coercing(OBJECT_COERCING)
        .build();

    private static boolean isNumberIsh(Object input) {
        return input instanceof Number || input instanceof String;
    }
    public static String typeName(Object input) {
        if (input == null) {
            return "null";
        }
        return input.getClass().getSimpleName();
    }
}
