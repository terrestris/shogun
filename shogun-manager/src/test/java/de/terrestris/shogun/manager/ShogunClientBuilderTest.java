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
package de.terrestris.shogun.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShogunClientBuilderTest {

    @Test
    public void builder() {
        assertNotNull(ShogunClientBuilder.builder());
    }

    @Test
    public void shogunServiceBaseUrl() {
        String url = "https://test/url";
        assertNotNull(ShogunClientBuilder.builder().shogunServiceBaseUrl(url));
    }

    @Test
    public void username() {
        String userName = "Peter";
        assertNotNull(ShogunClientBuilder.builder().username(userName));
    }

    @Test
    public void password() {
        String password = "password";
        assertNotNull(ShogunClientBuilder.builder().username(password));
    }

    @Test
    public void managerType() {
        ShogunManagerType type = ShogunManagerType.GEOSERVER_INTERCEPTOR;
        assertNotNull(ShogunClientBuilder.builder().managerType(type));
    }

    @Test
    public void build() {
        assertNotNull(ShogunClientBuilder.builder()
            .password("test")
            .username("peter")
            .shogunServiceBaseUrl("https://shogun")
            .managerType(ShogunManagerType.GEOSERVER_INTERCEPTOR)
            .build());
    }

    @Test
    public void build_unknownType() {
        assertNull(ShogunClientBuilder.builder()
            .password("test")
            .username("peter")
            .shogunServiceBaseUrl("https://shogun")
            .managerType(ShogunManagerType.APP)
            .build());
    }

    @Test
    public void build_TypeMissing() {
        assertThrows(IllegalStateException.class, () -> {
            ShogunClientBuilder.builder()
                .password("test")
                .username("peter")
                .shogunServiceBaseUrl("https://shogun")
                .build();
        });
    }

    @Test
    public void build_baseUrlRequired() {
        assertThrows(IllegalStateException.class, () -> {
            ShogunClientBuilder.builder()
                .password("test")
                .username("peter")
                .build();
        });
    }

    @Test
    public void build_usernamePasswordRequired() {
        assertThrows(IllegalStateException.class, () -> {
            ShogunClientBuilder.builder().build();
        });
    }
}
