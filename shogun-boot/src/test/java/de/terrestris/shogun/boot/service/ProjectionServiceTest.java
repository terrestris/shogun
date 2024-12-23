/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2024-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.boot.service;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.io.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectionServiceTest {

    @Test
    public void getProjectionTest() throws FactoryException {
        var service = new ProjectionService();
        var result = service.getProjectionDetails("EPSG:25832");
        assertEquals(1, result.getNumberResult());
        assertEquals("25832", result.getResults().getFirst().getCode());
    }

    @Test
    public void transformTest() throws FactoryException, TransformException, ParseException {
        var service = new ProjectionService();
        var result = service.transform("EPSG:4326", "EPSG:25832", "POINT(45 7)");
        assertTrue(result.getWkt().contains("342369"));
        assertTrue(result.getWkt().contains("4984896"));
        assertTrue(result.getWkt().startsWith("POINT"));
    }

}
