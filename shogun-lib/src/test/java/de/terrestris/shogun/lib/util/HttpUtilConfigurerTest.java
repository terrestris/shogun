/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2025-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("HttpUtilConfigurer Tests")
class HttpUtilConfigurerTest {

    private HttpUtilConfigurer httpUtilConfigurer;

    @BeforeEach
    void setUp() {
        httpUtilConfigurer = new HttpUtilConfigurer();
    }

    @Nested
    @DisplayName("configureHttpUtil")
    class ConfigureHttpUtil {

        @Test
        @DisplayName("Should configure HttpUtil with default timeout when no value is provided")
        void shouldConfigureHttpUtilWithDefaultTimeout() {
            ReflectionTestUtils.setField(httpUtilConfigurer, "httpTimeout", 15000);

            httpUtilConfigurer.configureHttpUtil();

            assertEquals(15000, HttpUtil.defaultHttpTimeout);
            assertEquals(15000, HttpUtil.httpTimeout);
        }

        @Test
        @DisplayName("Should configure HttpUtil with custom timeout when value is provided")
        void shouldConfigureHttpUtilWithCustomTimeout() {
            ReflectionTestUtils.setField(httpUtilConfigurer, "httpTimeout", 1909);

            httpUtilConfigurer.configureHttpUtil();

            assertEquals(1909, HttpUtil.defaultHttpTimeout);
            assertEquals(1909, HttpUtil.httpTimeout);
        }

        @Test
        @DisplayName("Should throw exception when timeout is negative")
        void shouldThrowExceptionWhenTimeoutIsNegative() {
            ReflectionTestUtils.setField(httpUtilConfigurer, "httpTimeout", -5000);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                httpUtilConfigurer.configureHttpUtil();
            });

            assertEquals("Timeout value cannot be negative", exception.getMessage());
        }
    }
}
