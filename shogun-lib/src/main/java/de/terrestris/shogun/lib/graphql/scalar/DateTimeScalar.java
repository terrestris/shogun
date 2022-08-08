/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.graphql.scalar;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.GraphQLScalarType;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@Log4j2
@Component
public class DateTimeScalar extends GraphQLScalarType {

    private static ObjectMapper om;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper){
        DateTimeScalar.om = objectMapper;
    }

    public DateTimeScalar() {
        this("DateTime", "A custom scalar that handles date formats");
    }

    public DateTimeScalar(String name, String description) {
        super(name, description, new Coercing() {
            @Override
            public Object serialize(Object dataFetcherResult) {
                return om.convertValue(dataFetcherResult, String.class);
            }

            @SneakyThrows
            @Override
            public Object parseValue(Object dataFetcherResult) {
                if (dataFetcherResult instanceof String) {
                    String dateTimeString = (String) dataFetcherResult;
                    try {
                        return OffsetDateTime.parse(dateTimeString);
                    } catch (DateTimeParseException e) {
                        throw new CoercingParseValueException("Unable to parse variable value " + dataFetcherResult + " as OffsetDateTime");
                    }
                }
                throw new CoercingParseValueException("Unable to parse variable value " + dataFetcherResult + " as OffsetDateTime");
            }

            @SneakyThrows
            @Override
            public Object parseLiteral(Object dataFetcherResult) {
                if (dataFetcherResult instanceof StringValue) {
                    String dateTimeString = ((StringValue) dataFetcherResult).getValue();;
                    try {
                        return OffsetDateTime.parse(dateTimeString);
                    } catch (DateTimeParseException e) {
                        throw new CoercingParseValueException("Unable to parse value " + dataFetcherResult + " as OffsetDateTime");
                    }
                }
                throw new CoercingParseLiteralException("Value is not OffsetDateTime");
            }
        });
    }

}
