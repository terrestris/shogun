/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2026-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.integration;

import de.terrestris.shogun.AbstractIT;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

class ProjectionIT extends AbstractIT {

  @Test
  void shouldReturnProjectionDetails() {
    given()
        .accept(ContentType.JSON)
        .queryParam("q", "EPSG:4326")
        .when()
        .get("/epsg")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("results[0].code", equalTo("4326"));
  }

  @Test
  void shouldTransformGeometryFromEpsg4326ToEpsg3857() {
    given()
        .accept(ContentType.JSON)
        .queryParam("source", "EPSG:4326")
        .queryParam("target", "EPSG:3857")
        .queryParam("wkt", "POINT (0 0)")
        .when()
        .get("/epsg/transform")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("wkt", containsString("POINT"));
  }
}
