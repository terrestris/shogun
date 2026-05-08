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
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class KeycloakIT extends AbstractIT {

  @Test
  void shouldReturnOkForValidKeycloakSession() {
    String token = getToken("admin", "admin");

    given()
        .auth().oauth2(token)
        .when()
        .get("/auth/isSessionValid")
        .then()
        .statusCode(200);
  }

  @Test
  void shouldReturnUnauthorizedForInvalidKeycloakSession() {
    given()
        .auth().oauth2("invalid-token")
        .when()
        .get("/auth/isSessionValid")
        .then()
        .statusCode(401);
  }
}
