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
import static org.hamcrest.Matchers.equalTo;

class PermissionsIT extends AbstractIT {

  private Long uploadTemporaryFile(String token) {
    return given()
        .auth().oauth2(token)
        .multiPart("file", "permissions-test.txt", "grant permission".getBytes(), "text/plain")
        .when()
        .post("/files/upload")
        .then()
        .statusCode(201)
        .contentType(ContentType.JSON)
        .extract()
        .jsonPath()
        .getLong("id");
  }

  @Test
  void shouldTogglePublicPermissionsForUploadedFile() {
    String token = getToken("admin", "admin");
    Long entityId = uploadTemporaryFile(token);

    given()
        .auth().oauth2(token)
        .accept(ContentType.JSON)
        .when()
        .get("/files/{id}/permissions/public", entityId)
        .then()
        .statusCode(200)
        .body("public", equalTo(false));

    given()
        .auth().oauth2(token)
        .when()
        .post("/files/{id}/permissions/public", entityId)
        .then()
        .statusCode(200);

    given()
        .auth().oauth2(token)
        .accept(ContentType.JSON)
        .when()
        .get("/files/{id}/permissions/public", entityId)
        .then()
        .statusCode(200)
        .body("public", equalTo(true));

    given()
        .auth().oauth2(token)
        .when()
        .delete("/files/{id}/permissions/public", entityId)
        .then()
        .statusCode(204);

    given()
        .auth().oauth2(token)
        .accept(ContentType.JSON)
        .when()
        .get("/files/{id}/permissions/public", entityId)
        .then()
        .statusCode(200)
        .body("public", equalTo(false));
  }
}
