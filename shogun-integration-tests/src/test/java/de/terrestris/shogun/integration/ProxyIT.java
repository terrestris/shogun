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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.restassured.RestAssured.given;

class ProxyIT extends AbstractIT {

  @Test
  void shouldReturnBadRequestWhenNoBaseUrlIsProvided() {
    String token = getToken("admin", "admin");

    given()
        .auth().oauth2(token)
        .accept(ContentType.TEXT)
        .when()
        .get("/proxy.action")
        .then()
        .statusCode(400);
  }

  @Test
  void shouldProxyGetRequestToWhiteListedHost() {
    wireMockClient.register(get(urlPathEqualTo("/test"))
        .withQueryParam("foo", equalTo("bar"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/plain")
            .withBody("proxy-response")));

    String token = getToken("admin", "admin");
    String targetUrl = "http://" + wiremock.getHost() + ":" + wiremock.getMappedPort(8080) + "/test";

    given()
        .auth().oauth2(token)
        .queryParam("baseUrl", targetUrl)
        .queryParam("foo", "bar")
        .when()
        .get("/proxy.action")
        .then()
        .statusCode(200)
        .contentType("text/plain")
        .body(org.hamcrest.Matchers.equalTo("proxy-response"));
  }
}
