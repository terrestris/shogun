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
package de.terrestris.shogun;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import de.terrestris.shogun.boot.config.ApplicationConfig;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import com.github.tomakehurst.wiremock.client.WireMock;

import java.util.List;

@SuppressWarnings("resource")
@SpringBootTest(classes = ApplicationConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractIT {

    static final PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgis/postgis:16-3.4-alpine")
                    .asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("shogun")
            .withUsername("shogun")
            .withPassword("shogun");

    protected static final KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.0.8")
            .withRealmImportFile("SHOGun-realm.json");

    protected static final WireMockContainer wiremock = new WireMockContainer("wiremock/wiremock:3.13.2");
    protected static final WireMock wireMockClient;

    static {
        postgres.start();
        keycloak.start();
        wiremock.start();

        wireMockClient = new WireMock(wiremock.getHost(), wiremock.getMappedPort(8080));

        System.setProperty("spring.datasource.url",
                postgres.getJdbcUrl() + "?currentSchema=shogun,shogun_rev");
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        System.setProperty("keycloak.server-url", keycloak.getAuthServerUrl());
        System.setProperty("keycloak.admin-client-secret", "test-secret");
        System.setProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                keycloak.getAuthServerUrl() + "/realms/SHOGun");
        System.setProperty("wiremock.base-url",
                "http://" + wiremock.getHost() + ":" + wiremock.getMappedPort(8080));
        System.setProperty("shogun-proxy.whitelist",
                wiremock.getHost() + ":" + wiremock.getMappedPort(8080));
        System.setProperty("interceptor.defaultOwsUrl",
                "http://" + wiremock.getHost() + ":" + wiremock.getMappedPort(8080) + "/ows");
    }

    @LocalServerPort
    int port;

    @BeforeAll
    static void setupContainers() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "";
    }

    @BeforeEach
    void setupPort() {
        WireMock.configureFor(wiremock.getHost(), wiremock.getMappedPort(8080));
        WireMock.reset();
        RestAssured.port = port;
        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 10000)
                        .setParam("http.socket.timeout", 10000));
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> postgres.getJdbcUrl() + "?currentSchema=shogun,shogun_rev");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("keycloak.server-url", keycloak::getAuthServerUrl);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/SHOGun");
        registry.add("wiremock.base-url",
                () -> "http://" + wiremock.getHost() + ":" + wiremock.getMappedPort(8080));
        registry.add("shogun-proxy.whitelist",
                () -> List.of(wiremock.getHost() + ":" + wiremock.getMappedPort(8080)));
        registry.add("interceptor.defaultOwsUrl",
                () -> "http://" + wiremock.getHost() + ":" + wiremock.getMappedPort(8080) + "/ows");
        registry.add("spring.datasource.url",
                () -> postgres.getJdbcUrl() + "?currentSchema=shogun,interceptor,shogun_rev,public");
    }

    protected String getToken(String username, String password) {
        String tokenUrl = keycloak.getAuthServerUrl()
                + "/realms/SHOGun/protocol/openid-connect/token";

        return RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("client_id", "shogun-boot")
                .formParam("client_secret", "test-secret")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .post(tokenUrl)
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }
}
