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

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.hamcrest.Matchers.notNullValue;

class FilesIT extends AbstractIT {

  @Test
  void shouldUploadDownloadAndDeleteFile() {
    String token = getToken("admin", "admin");
    String content = "downloadable file content";

    String fileUuid = given()
        .auth().oauth2(token)
        .multiPart("file", "test.txt", content.getBytes(), "text/plain")
        .when()
        .post("/files/upload")
        .then()
        .statusCode(201)
        .contentType(ContentType.JSON)
        .body("fileName", notNullValue())
        .body("fileUuid", notNullValue())
        .extract()
        .jsonPath()
        .getString("fileUuid");

    byte[] downloaded = given()
        .auth().oauth2(token)
        .when()
        .get("/files/{fileUuid}", UUID.fromString(fileUuid))
        .then()
        .statusCode(200)
        .contentType("text/plain")
        .extract()
        .asByteArray();

    assertArrayEquals(content.getBytes(), downloaded);

    given()
        .auth().oauth2(token)
        .when()
        .delete("/files/{fileUuid}", UUID.fromString(fileUuid))
        .then()
        .statusCode(204);
  }

  @Test
  void shouldReturnFilesPage() {
    given()
        .accept(ContentType.JSON)
        .when()
        .get("/files")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("content", notNullValue());
  }

  @Test
  void shouldRejectUnsupportedContentType() {
    String token = getToken("admin", "admin");

    given()
        .auth().oauth2(token)
        .multiPart("file", "test.yml", "unsupported content".getBytes(), "application/yml")
        .when()
        .post("/files/upload")
        .then()
        .statusCode(500);
  }

  @Test
  void shouldRejectUnauthenticatedUpload() {
    given()
        .multiPart("file", "test.txt", "content".getBytes(), "text/plain")
        .when()
        .post("/files/upload")
        .then()
        .statusCode(401);
  }
}
