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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.StringValue;
import graphql.schema.*;
import lombok.extern.log4j.Log4j2;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import java.util.HashMap;

@Log4j2
@Component
public class GeometryScalar extends GraphQLScalarType {

    private static ObjectMapper om;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper){
        GeometryScalar.om = objectMapper;
    }

    public GeometryScalar() {
        this("Geometry", "A custom scalar that handles geometries");
    }

    public GeometryScalar(String name, String description) {
        super(name, description, new Coercing() {

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
        });
    }

    static boolean isAGeometry(Object possibleGeometryValue) {
        return possibleGeometryValue instanceof Geometry;
    }
}
