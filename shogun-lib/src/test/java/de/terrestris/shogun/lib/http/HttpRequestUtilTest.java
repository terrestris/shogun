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
package de.terrestris.shogun.lib.http;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.hc.core5.http.Header;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("HttpRequestUtil Tests")
class HttpRequestUtilTest {

    private static HttpServletRequest request(String method, String contentType) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getContentType()).thenReturn(contentType);
        return request;
    }

    @Test
    @DisplayName("Should detect sane requests")
    void shouldDetectSaneRequests() {
        assertFalse(HttpRequestUtil.isSaneRequest(null));
        assertFalse(HttpRequestUtil.isSaneRequest(request(null, null)));
        assertTrue(HttpRequestUtil.isSaneRequest(request("GET", null)));
    }

    @Test
    @DisplayName("Should detect GET and POST requests")
    void shouldDetectGetAndPost() {
        assertTrue(HttpRequestUtil.isHttpGetRequest(request("GET", null)));
        assertFalse(HttpRequestUtil.isHttpGetRequest(request("POST", null)));
        assertFalse(HttpRequestUtil.isHttpGetRequest(null));
        assertTrue(HttpRequestUtil.isHttpPostRequest(request("POST", null)));
        assertFalse(HttpRequestUtil.isHttpPostRequest(request("GET", null)));
    }

    @Test
    @DisplayName("Should detect form/multipart POST requests")
    void shouldDetectFormMultipartPost() {
        assertTrue(HttpRequestUtil.isFormMultipartPost(request("POST", "multipart/form-data; boundary=x")));
        assertTrue(HttpRequestUtil.isFormMultipartPost(request("POST", "Multipart/Form-Data")));
        assertFalse(HttpRequestUtil.isFormMultipartPost(request("POST", "text/plain")));
        assertFalse(HttpRequestUtil.isFormMultipartPost(request("POST", null)));
        assertFalse(HttpRequestUtil.isFormMultipartPost(request("GET", "multipart/form-data")));
    }

    @Test
    @DisplayName("Should extract headers and skip empty values")
    void shouldExtractHeaders() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Accept", "X-Empty")));
        when(request.getHeaders("Accept")).thenReturn(Collections.enumeration(List.of("a", "b")));
        when(request.getHeaders("X-Empty")).thenReturn(Collections.enumeration(List.of("")));

        Header[] headers = HttpRequestUtil.getHeadersFromRequest(request);

        assertEquals(2, headers.length);
        assertEquals("Accept", headers[0].getName());
        assertEquals("a", headers[0].getValue());
        assertEquals("b", headers[1].getValue());
    }

    @Test
    @DisplayName("Should build the application URI from the request")
    void shouldBuildApplicationUri() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("/shogun");

        assertEquals(URI.create("http://localhost:8080/shogun"), HttpRequestUtil.getApplicationURIFromRequest(request));
    }
}
