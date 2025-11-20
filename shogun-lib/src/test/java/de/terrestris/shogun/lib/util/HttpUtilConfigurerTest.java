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
