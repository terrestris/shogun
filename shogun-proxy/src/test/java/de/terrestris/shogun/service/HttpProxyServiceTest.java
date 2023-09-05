/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2021-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.service;

import de.terrestris.shogun.config.HttpProxyConfig;
import de.terrestris.shogun.lib.dto.HttpResponse;
import de.terrestris.shogun.lib.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.HttpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = HttpProxyConfig.class)
@ActiveProfiles("proxy-test")
public class HttpProxyServiceTest {

    @Autowired
    protected HttpProxyService httpProxyService;

    @Test
    @DisplayName("Return status code 400 if request and base URL are null")
    public void proxy_returns_400_when_no_url_is_given() {
        final ResponseEntity<?> responseEntity = httpProxyService.doProxy(null, null, null);
        assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_400_NO_URL, responseEntity.getBody());
    }

    @Test
    @DisplayName("Return status code 400 if base URL is empty")
    public void proxy_returns_400_when_baseUrl_is_empty() {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String baseUrl = StringUtils.EMPTY;
        final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
        assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_400_NO_URL, responseEntity.getBody());
    }

    @Test
    @DisplayName("Return status code 500 if base URL is not a valid URL")
    public void proxy_returns_500_when_baseUrl_is_no_valid_URL() {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String baseUrl = "$$$$___S04___$$$$";
        final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
        assertEquals("Returned Status code matched mocked one.", HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_500, responseEntity.getBody());
    }

    @Test
    @DisplayName("Return status code 502 if base URL is not in whitelist")
    public void proxy_returns_502_when_baseUrl_is_not_in_URL_whitelist() {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String baseUrl = "https://unallowedHost.com/unallowedPath";

        final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
        assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_GATEWAY, responseEntity.getStatusCode());
        assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_502, responseEntity.getBody());
    }

    @DisplayName("Return status code 405 for unsupported HTTP methods")
    @ParameterizedTest
    @ValueSource(strings = {"DELETE", "PUT", "HEAD", "PATCH", "TRACE", "OPTIONS"})
    public void proxy_returns_405_for_unsupported_HTTP_methods(String unsupportedMethod) {
        final String baseUrl = "https://www.terrestris.de/internet.txt";
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getMethod()).thenReturn(unsupportedMethod);
        final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
        assertEquals("Returned Status code matched mocked one.", HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
        assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_405, responseEntity.getBody());
    }

    @Test
    @DisplayName("Return status code 200 for allowed HTTP GET request")
    public void proxy_returns_200_for_allowed_GET_request() throws URISyntaxException {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/internet.txt";
        final URI baseUri = new URI(baseUrl);
        HttpResponse mockedResponse = mock(HttpResponse.class);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        when(mockedResponse.getHeaders()).thenReturn(headers);
        when(mockedResponse.getBody()).thenReturn(internetContent.getBytes());
        when(mockedResponse.getStatusCode()).thenReturn(status);

        try (
            MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)
        ) {
            httpUtilMock.when(() -> HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(true);
            httpUtilMock.when(() -> HttpUtil.forwardGet(baseUri, mockedRequest, false)).thenReturn(mockedResponse);

            final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
            assertEquals("Returned Status code matched mocked one.", HttpStatus.OK, responseEntity.getStatusCode());

            final byte[] responseBodyAsByteArray = (byte[]) responseEntity.getBody();
            assertNotNull("Response body is null.", responseBodyAsByteArray);
            assert responseBodyAsByteArray != null;
            assertEquals("Returned content matched mocked one.", new String(responseBodyAsByteArray), internetContent);
        }
    }

    @Test
    @DisplayName("Return status code 400 for erroneous HTTP GET request")
    public void proxy_returns_400_for_erroneous_GET_requests() throws URISyntaxException {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/internet.txt";
        final URI baseUri = new URI(baseUrl);
        HttpResponse mockedResponse = mock(HttpResponse.class);
        final String msg = "ERROR";

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        when(mockedResponse.getHeaders()).thenReturn(headers);
        when(mockedResponse.getBody()).thenReturn(internetContent.getBytes());
        when(mockedResponse.getStatusCode()).thenReturn(status);

        try (
            MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)
        ) {
            httpUtilMock.when(() -> HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(true);
            httpUtilMock.when(() -> HttpUtil.forwardGet(baseUri, mockedRequest, false)).thenThrow(new HttpException(msg));
            final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
            assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            assertEquals("Returned Status matched", HttpProxyService.ERR_MSG_400_COMMON, responseEntity.getBody());
        }
    }

    @Test
    @DisplayName("Return status code 200 for allowed HTTP POST request")
    public void proxy_returns_200_for_allowed_POST_request() throws URISyntaxException {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/endpointToPostAt";
        final URI baseUri = new URI(baseUrl);
        HttpResponse mockedResponse = mock(HttpResponse.class);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        when(mockedResponse.getHeaders()).thenReturn(headers);
        when(mockedResponse.getBody()).thenReturn(internetContent.getBytes());
        when(mockedResponse.getStatusCode()).thenReturn(status);

        try (
            MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)
        ) {
            httpUtilMock.when(() -> HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
            httpUtilMock.when(() -> HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
            httpUtilMock.when(() -> HttpUtil.forwardPost(baseUri, mockedRequest, false)).thenReturn(mockedResponse);
            final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
            assertEquals("Returned Status code matched mocked one.", HttpStatus.OK, responseEntity.getStatusCode());

            final byte[] responseBodyAsByteArray = (byte[]) responseEntity.getBody();
            assertNotNull("Response body is null.", responseBodyAsByteArray);
            assert responseBodyAsByteArray != null;

            assertEquals("Returned content matched mocked one.", new String(responseBodyAsByteArray), internetContent);
        }
    }

    @Test
    @DisplayName("Return status code 400 for erroneous HTTP POST request")
    public void proxy_returns_400_for_erroneous_POST_requests() throws URISyntaxException {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/internet.txt";
        final URI baseUri = new URI(baseUrl);
        HttpResponse mockedResponse = mock(HttpResponse.class);
        final String msg = "ERROR";

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        when(mockedResponse.getHeaders()).thenReturn(headers);
        when(mockedResponse.getBody()).thenReturn(internetContent.getBytes());
        when(mockedResponse.getStatusCode()).thenReturn(status);

        try (
            MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)
        ) {
            httpUtilMock.when(() -> HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
            httpUtilMock.when(() -> HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
            httpUtilMock.when(() -> HttpUtil.forwardPost(baseUri, mockedRequest, false)).thenThrow(new HttpException(msg));
            final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
            assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            assertEquals("Returned Status matched", HttpProxyService.ERR_MSG_400_COMMON, responseEntity.getBody());
        }
    }

    @Test
    @DisplayName("Return status code 200 for allowed HTTP multipart POST request")
    public void proxy_returns_200_for_allowed_FormMultipartPost_request() throws URISyntaxException {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/endpointToPostAt";
        final URI baseUri = new URI(baseUrl);
        HttpResponse mockedResponse = mock(HttpResponse.class);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        when(mockedResponse.getHeaders()).thenReturn(headers);
        when(mockedResponse.getBody()).thenReturn(internetContent.getBytes());
        when(mockedResponse.getStatusCode()).thenReturn(status);

        try (
            MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)
        ) {
            httpUtilMock.when(() -> HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
            httpUtilMock.when(() -> HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
            httpUtilMock.when(() -> HttpUtil.isFormMultipartPost(mockedRequest)).thenReturn(true);
            httpUtilMock.when(() -> HttpUtil.forwardFormMultipartPost(baseUri, mockedRequest, false)).thenReturn(mockedResponse);
            final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
            assertEquals("Returned Status code matched mocked one.", HttpStatus.OK, responseEntity.getStatusCode());

            final byte[] responseBodyAsByteArray = (byte[]) responseEntity.getBody();
            assertNotNull("Response body is null.", responseBodyAsByteArray);
            assert responseBodyAsByteArray != null;
            assertEquals("Returned content matched mocked one.", new String(responseBodyAsByteArray), internetContent);
        }
    }

    @Test
    @DisplayName("Return status code 400 for erroneous HTTP multipart POST request")
    public void proxy_returns_400_for_erroneous_FormMultipartPost_requests() throws URISyntaxException {
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/internet.txt";
        final URI baseUri = new URI(baseUrl);
        HttpResponse mockedResponse = mock(HttpResponse.class);
        final String msg = "ERROR";

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        when(mockedResponse.getHeaders()).thenReturn(headers);
        when(mockedResponse.getBody()).thenReturn(internetContent.getBytes());
        when(mockedResponse.getStatusCode()).thenReturn(status);
        try (
            MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)
        ) {
            httpUtilMock.when(() -> HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
            httpUtilMock.when(() -> HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
            httpUtilMock.when(() -> HttpUtil.isFormMultipartPost(mockedRequest)).thenReturn(true);
            httpUtilMock.when(() -> HttpUtil.forwardFormMultipartPost(baseUri, mockedRequest, false)).thenThrow(new HttpException(msg));

            final ResponseEntity<?> responseEntity = httpProxyService.doProxy(mockedRequest, baseUrl, null);
            assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            assertEquals("Returned Status matched", HttpProxyService.ERR_MSG_400_COMMON, responseEntity.getBody());
        }
    }
}
