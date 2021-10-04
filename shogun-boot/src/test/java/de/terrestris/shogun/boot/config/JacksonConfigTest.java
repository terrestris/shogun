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
package de.terrestris.shogun.boot.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shogun.boot.runner.ApplicationInitializer;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.LineString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {
        ApplicationConfig.class,
        JdbcConfiguration.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class JacksonConfigTest {

    // Replace ApplicationInitializer by functionless mock
    @MockBean
    private ApplicationInitializer applicationInitializer;

    @Value("${shogun.srid}")
    private int srid;

    // The objectmapper created during boot procedure
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void assertThatObjectMapperHasJtsModuleRegistered() {
        var registeredModules = objectMapper.getRegisteredModuleIds();
        boolean hasJtsModuleRegistered = false;
        for (Object module : registeredModules) {
            if (StringUtils.equalsIgnoreCase((CharSequence) module, "com.bedatadriven.jackson.datatype.jts.JtsModule")) {
                hasJtsModuleRegistered = true;
            }
        }
        Assertions.assertTrue(hasJtsModuleRegistered, "JTS Module is not registrered in ObjectMapper");
    }

    @Test
    public void assertThatObjectMapperHasCorrectlyConfiguredJtsModule() throws JsonProcessingException {
        String LINESTRING_25832 = """
            {
                "type": "LineString",
                "coordinates": [
                    [
                        711957.369742162,
                        5637657.304058334
                    ],
                    [
                        711979.3599907478,
                        5637629.050147795
                    ],
                    [
                        712000.5099907471,
                        5637596.860147787
                    ]
                ]
            }
            """;

        LineString parsedLineString = objectMapper.readValue(LINESTRING_25832, LineString.class);
        Assertions.assertEquals(parsedLineString.getSRID(), srid, "Coordinate reference system of parsed " +
            "geometry does not match the one configured in JTS Module");
    }

}
