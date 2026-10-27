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
import static org.hamcrest.Matchers.notNullValue;

class ApplicationInfoIT extends AbstractIT {

  @Test
  void shouldReturnApplicationInfo() {
    String token = getToken("admin", "admin");

    given()
        .auth().oauth2(token)
        .accept(ContentType.JSON)
        .when()
        .get("/info/app")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("$", notNullValue());
  }
}
