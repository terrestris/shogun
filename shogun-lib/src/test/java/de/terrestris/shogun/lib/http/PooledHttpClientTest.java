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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import de.terrestris.shogun.lib.dto.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("PooledHttpClient Tests")
class PooledHttpClientTest {

    private record RecordedRequest(String method, URI uri, Headers headers, String body) {
    }

    private final AtomicReference<RecordedRequest> lastRequest = new AtomicReference<>();

    private HttpServer server;
    private HttpServer proxyServer;
    private PooledHttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        // make sure local test servers are never routed through a proxy configured in the environment
        System.setProperty("http.nonProxyHosts", "127.0.0.1");

        server = startServer("pong", lastRequest);
        client = createClient(15000);
    }

    @AfterEach
    void tearDown() {
        client.shutdown();
        server.stop(0);
        if (proxyServer != null) {
            proxyServer.stop(0);
        }
        System.clearProperty("http.nonProxyHosts");
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
    }

    @Test
    @DisplayName("Should reject a negative timeout")
    void shouldRejectNegativeTimeout() {
        PooledHttpClient invalid = new PooledHttpClient();
        ReflectionTestUtils.setField(invalid, "httpTimeout", -5000);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, invalid::init);

        assertEquals("Timeout value cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should perform a GET request and map status, headers and body")
    void shouldPerformGet() throws Exception {
        Header[] headers = {new BasicHeader("X-Custom", "abc")};

        HttpResponse response = client.get(uri("/resource?a=b"), headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("pong", new String(response.getBody(), StandardCharsets.UTF_8));
        assertEquals("yes", response.getHeaders().getFirst("X-Test"));
        RecordedRequest request = lastRequest.get();
        assertEquals("GET", request.method());
        assertEquals("/resource?a=b", request.uri().toString());
        assertEquals("abc", request.headers().getFirst("X-Custom"));
    }

    @Test
    @DisplayName("Should perform a POST request with a string body")
    void shouldPostStringBody() throws Exception {
        HttpResponse response = client.post(uri("/wfs"), "<xml/>", ContentType.APPLICATION_XML, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        RecordedRequest request = lastRequest.get();
        assertEquals("POST", request.method());
        assertEquals("<xml/>", request.body());
        assertTrue(request.headers().getFirst("Content-Type").startsWith("application/xml"));
    }

    @Test
    @DisplayName("Should send form parameters UTF-8 encoded")
    void shouldPostFormParamsUtf8Encoded() throws Exception {
        List<NameValuePair> params = List.of(new BasicNameValuePair("name", "Müller"));

        client.post(uri("/form"), params, null);

        RecordedRequest request = lastRequest.get();
        assertEquals("name=M%C3%BCller", request.body());
        assertTrue(request.headers().getFirst("Content-Type").startsWith("application/x-www-form-urlencoded"));
    }

    @Test
    @DisplayName("Should send a file as multipart POST")
    void shouldPostFileAsMultipart(@TempDir Path tempDir) throws Exception {
        Path file = Files.writeString(tempDir.resolve("data.txt"), "file-content");

        client.post(uri("/upload"), file.toFile(), null, null);

        RecordedRequest request = lastRequest.get();
        assertTrue(request.headers().getFirst("Content-Type").startsWith("multipart/form-data"));
        assertTrue(request.body().contains("name=\"file\""));
        assertTrue(request.body().contains("file-content"));
    }

    @Test
    @DisplayName("Should perform PUT and DELETE requests")
    void shouldPerformPutAndDelete() throws Exception {
        client.put(uri("/item"), "{\"a\":1}", ContentType.APPLICATION_JSON, null, false);
        assertEquals("PUT", lastRequest.get().method());
        assertEquals("{\"a\":1}", lastRequest.get().body());

        client.delete(uri("/item"), null);
        assertEquals("DELETE", lastRequest.get().method());
    }

    @Test
    @DisplayName("Should not send a PUT body without content type")
    void shouldNotSendPutBodyWithoutContentType() throws Exception {
        client.put(uri("/item"), "ignored", null, null, false);

        assertEquals("", lastRequest.get().body());
    }

    @Test
    @DisplayName("Should send preemptive basic auth when credentials are given")
    void shouldSendBasicAuth() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("user", "pw".toCharArray());

        client.get(uri("/secured"), credentials, null);

        String expected = "Basic " + Base64.getEncoder().encodeToString("user:pw".getBytes(StandardCharsets.UTF_8));
        assertEquals(expected, lastRequest.get().headers().getFirst("Authorization"));
    }

    @Test
    @DisplayName("Should not send basic auth without credentials")
    void shouldNotSendAuthWithoutCredentials() throws Exception {
        client.get(uri("/open"), null);

        assertNull(lastRequest.get().headers().getFirst("Authorization"));
    }

    @Test
    @DisplayName("Should use the system proxy for hosts that are not excluded")
    void shouldUseSystemProxy() throws Exception {
        AtomicReference<RecordedRequest> proxyRequest = new AtomicReference<>();
        proxyServer = startServer("via-proxy", proxyRequest);
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", String.valueOf(proxyServer.getAddress().getPort()));

        HttpResponse response = client.get(URI.create("http://shogun-proxy-test.invalid/resource"), null);

        assertEquals("via-proxy", new String(response.getBody(), StandardCharsets.UTF_8));
        assertEquals("shogun-proxy-test.invalid", proxyRequest.get().headers().getFirst("Host"));
        assertNull(lastRequest.get());
    }

    @Test
    @DisplayName("Should bypass the system proxy for hosts matching http.nonProxyHosts")
    void shouldBypassProxyForNonProxyHosts() throws Exception {
        AtomicReference<RecordedRequest> proxyRequest = new AtomicReference<>();
        proxyServer = startServer("via-proxy", proxyRequest);
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", String.valueOf(proxyServer.getAddress().getPort()));

        HttpResponse response = client.get(uri("/direct"), null);

        assertEquals("pong", new String(response.getBody(), StandardCharsets.UTF_8));
        assertNull(proxyRequest.get());
    }

    @Test
    @DisplayName("Should wrap connection errors in an HttpException with cause")
    void shouldWrapConnectionErrors() throws IOException {
        HttpServer closed = startServer("unused", new AtomicReference<>());
        URI closedUri = URI.create("http://127.0.0.1:" + closed.getAddress().getPort() + "/gone");
        closed.stop(0);

        HttpException exception = assertThrows(HttpException.class, () -> client.get(closedUri, null));

        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("Should forward a GET including the request headers")
    void shouldForwardGetWithHeaders() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(List.of("X-Custom")));
        when(request.getHeaders("X-Custom")).thenReturn(Collections.enumeration(List.of("abc")));

        client.forwardGet(uri("/forward"), request, true);

        assertEquals("abc", lastRequest.get().headers().getFirst("X-Custom"));
    }

    @Test
    @DisplayName("Should forward a POST with the body of the request")
    void shouldForwardPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContentType()).thenReturn("text/plain; charset=UTF-8");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("hello")));

        client.forwardPost(uri("/forward"), request, false);

        assertEquals("POST", lastRequest.get().method());
        assertEquals("hello", lastRequest.get().body());
    }

    private URI uri(String pathAndQuery) {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + pathAndQuery);
    }

    private static PooledHttpClient createClient(int timeout) {
        PooledHttpClient pooledHttpClient = new PooledHttpClient();
        ReflectionTestUtils.setField(pooledHttpClient, "httpTimeout", timeout);
        ReflectionTestUtils.setField(pooledHttpClient, "maxTotal", 10);
        ReflectionTestUtils.setField(pooledHttpClient, "maxPerRoute", 5);
        ReflectionTestUtils.setField(pooledHttpClient, "evictIdleSeconds", 5);
        pooledHttpClient.init();
        return pooledHttpClient;
    }

    private static HttpServer startServer(String responseBody, AtomicReference<RecordedRequest> recorder)
        throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        httpServer.createContext("/", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            recorder.set(new RecordedRequest(
                exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRequestHeaders(), body
            ));
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("X-Test", "yes");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        httpServer.start();
        return httpServer;
    }
}
