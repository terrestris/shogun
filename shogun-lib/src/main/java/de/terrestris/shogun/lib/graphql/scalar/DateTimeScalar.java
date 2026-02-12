/*
 * Modified work - Copyright 2018-present the original author or authors.
 * See https://raw.githubusercontent.com/graphql-java/graphql-java-extended-scalars/master/src/main/java/graphql/scalars/datetime/DateTimeScalar.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.graphql.scalar;

import graphql.GraphQLContext;
import graphql.Internal;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import org.jspecify.annotations.NonNull;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.function.Function;

import static graphql.scalars.util.Kit.typeName;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;

/**
 * Access this via {@link graphql.scalars.ExtendedScalars#DateTime}
 *
 * Added parser / serializer for {@link java.time.Instant} based on <a href="https://github.com/graphql-java/graphql-java-extended-scalars/blob/master/src/main/java/graphql/scalars/datetime/DateTimeScalar.java">this implementation</a>
 *
 */
@Internal
public final class DateTimeScalar {

    public static final GraphQLScalarType INSTANCE;

    private DateTimeScalar() {}
    private static final DateTimeFormatter customOutputFormatter = getCustomDateTimeFormatter();

    static {
        Coercing<OffsetDateTime, String> coercing = new Coercing<OffsetDateTime, String>() {
            @Override
            public String serialize(@NonNull Object dataFetcherResult, @NonNull GraphQLContext graphQLContext, @NonNull Locale locale) throws CoercingSerializeException {
                OffsetDateTime offsetDateTime;
                if (dataFetcherResult instanceof OffsetDateTime) {
                    offsetDateTime = (OffsetDateTime) dataFetcherResult;
                } else if (dataFetcherResult instanceof ZonedDateTime) {
                    offsetDateTime = ((ZonedDateTime) dataFetcherResult).toOffsetDateTime();
                } else if (dataFetcherResult instanceof Instant instant) {
                    offsetDateTime = instant.atOffset(OffsetDateTime.now().getOffset());
                } else if (dataFetcherResult instanceof String) {
                    offsetDateTime = parseOffsetDateTime(dataFetcherResult.toString(), CoercingSerializeException::new);
                } else {
                    throw new CoercingSerializeException(
                        "Expected something we can convert to 'java.time.OffsetDateTime' but was '" + typeName(dataFetcherResult) + "'."
                    );
                }
                try {
                    return customOutputFormatter.format(offsetDateTime);
                } catch (DateTimeException e) {
                    throw new CoercingSerializeException(
                        "Unable to turn TemporalAccessor into OffsetDateTime because of : '" + e.getMessage() + "'."
                    );
                }
            }

            @Override
            public OffsetDateTime parseValue(@NonNull Object input, @NonNull GraphQLContext graphQLContext, @NonNull Locale locale) throws CoercingParseValueException {
                OffsetDateTime offsetDateTime;
                if (input instanceof OffsetDateTime) {
                    offsetDateTime = (OffsetDateTime) input;
                } else if (input instanceof ZonedDateTime) {
                    offsetDateTime = ((ZonedDateTime) input).toOffsetDateTime();
                } else if (input instanceof Instant instant) {
                    offsetDateTime = instant.atOffset(OffsetDateTime.now().getOffset());
                } else if (input instanceof String) {
                    offsetDateTime = parseOffsetDateTime(input.toString(), CoercingParseValueException::new);
                } else {
                    throw new CoercingParseValueException(
                        "Expected a 'String' but was '" + typeName(input) + "'."
                    );
                }
                return offsetDateTime;
            }

            @Override
            public OffsetDateTime parseLiteral(@NonNull Value<?> input, @NonNull CoercedVariables variables, @NonNull GraphQLContext graphQLContext, @NonNull Locale locale) throws CoercingParseLiteralException {
                if (!(input instanceof StringValue)) {
                    throw new CoercingParseLiteralException(
                        "Expected AST type 'StringValue' but was '" + typeName(input) + "'."
                    );
                }
                return parseOffsetDateTime(((StringValue) input).getValue(), CoercingParseLiteralException::new);
            }

            @Override
            @NonNull
            public Value<?> valueToLiteral(@NonNull Object input, @NonNull GraphQLContext graphQLContext, @NonNull Locale locale) {
                String s = serialize(input, graphQLContext, locale);
                return StringValue.newStringValue(s).build();
            }

            private OffsetDateTime parseOffsetDateTime(String s, Function<String, RuntimeException> exceptionMaker) {
                try {
                    OffsetDateTime parse = OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    if (parse.get(OFFSET_SECONDS) == 0 && s.endsWith("-00:00")) {
                        throw exceptionMaker.apply("Invalid value : '" + s + "'. Negative zero offset is not allowed");
                    }
                    return parse;
                } catch (DateTimeParseException e) {
                    throw exceptionMaker.apply("Invalid RFC3339 value : '" + s + "'. because of : '" + e.getMessage() + "'");
                }
            }
        };

        INSTANCE = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("A slightly refined version of RFC-3339 compliant DateTime Scalar")
            .specifiedByUrl("https://scalars.graphql.org/andimarek/date-time") // TODO: Change to .specifiedByURL when builder added to graphql-java
            .coercing(coercing)
            .build();
    }

    private static DateTimeFormatter getCustomDateTimeFormatter() {
        return new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendFraction(NANO_OF_SECOND, 3, 3, true)
            .appendOffset("+HH:MM", "Z")
            .toFormatter();
    }

}
