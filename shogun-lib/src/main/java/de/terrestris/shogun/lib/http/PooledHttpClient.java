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

import de.terrestris.shogun.lib.dto.HttpResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * A shared, pooled {@link CloseableHttpClient} exposed as a Spring bean.
 *
 * This enables:
 * <ul>
 *   <li>Connection reuse / HTTP Keep-Alive</li>
 *   <li>Bounded pool of sockets (no unbounded resource growth)</li>
 *   <li>Fewer TCP handshakes / TLS negotiations → lower latency</li>
 *   <li>No per-request allocation of connection manager, thread pools etc.</li>
 * </ul>
 *
 * <p>Intended for use in proxy scenarios where the same downstream host is
 * hit repeatedly (e.g. GeoServer interceptor). The current API is intentionally
 * minimal – it only exposes what the interceptor needs. Extend as required.
 *
 * <p><b>Usage:</b>
 * <pre>{@code
 * @Autowired
 * private PooledHttpClient pooledHttpClient;
 *
 * HttpResponse resp = pooledHttpClient.get(uri, headers);
 * }</pre>
 */
@Component
@Log4j2
public class PooledHttpClient {

    @Value("${http.timeout:15000}")
    private int httpTimeout;

    /**
     * Maximum total number of connections held in the pool.
     */
    @Value("${http.pool.maxTotal:200}")
    private int maxTotal;

    /**
     * Maximum number of connections per route (host).
     */
    @Value("${http.pool.maxPerRoute:50}")
    private int maxPerRoute;

    /**
     * How long an idle connection may sit in the pool before being evicted.
     */
    @Value("${http.pool.evictIdleSeconds:30}")
    private int evictIdleSeconds;

    private PoolingHttpClientConnectionManager connectionManager;
    private CloseableHttpClient httpClient;

    @PostConstruct
    public void init() {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setConnectTimeout(Timeout.ofMilliseconds(httpTimeout))
            .build();

        connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setDefaultConnectionConfig(connectionConfig)
            .setMaxConnTotal(maxTotal)
            .setMaxConnPerRoute(maxPerRoute)
            .build();

        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(httpTimeout))
            .setResponseTimeout(Timeout.ofMilliseconds(httpTimeout))
            .build();

        httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .evictIdleConnections(TimeValue.ofSeconds(evictIdleSeconds))
            .evictExpiredConnections()
            .build();

        log.info(
            "Initialized PooledHttpClient (timeout={}ms, maxTotal={}, maxPerRoute={}, evictIdleSeconds={})",
            httpTimeout, maxTotal, maxPerRoute, evictIdleSeconds
        );
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down PooledHttpClient");
        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            log.warn("Error while closing shared HttpClient: {}", e.getMessage());
        }
        if (connectionManager != null) {
            connectionManager.close();
        }
    }

    /**
     * Perform a GET request.
     */
    public HttpResponse get(URI uri, Header[] requestHeaders) throws HttpException {
        HttpGet request = new HttpGet(uri);
        applyHeaders(request, requestHeaders);
        return execute(request);
    }

    /**
     * Perform a POST request with a raw string body (e.g. XML for WFS-T).
     */
    public HttpResponse post(URI uri, String body, ContentType contentType, Header[] requestHeaders)
        throws HttpException {
        HttpPost request = new HttpPost(uri);
        applyHeaders(request, requestHeaders);
        if (body != null) {
            request.setEntity(new StringEntity(body, contentType));
        }
        return execute(request);
    }

    /**
     * Perform a POST request with form parameters (application/x-www-form-urlencoded).
     */
    public HttpResponse post(URI uri, List<NameValuePair> params, Header[] requestHeaders)
        throws HttpException {
        HttpPost request = new HttpPost(uri);
        applyHeaders(request, requestHeaders);
        if (params != null && !params.isEmpty()) {
            request.setEntity(new UrlEncodedFormEntity(params));
        }
        return execute(request);
    }

    private void applyHeaders(HttpUriRequestBase request, Header[] headers) {
        if (headers != null) {
            request.setHeaders(headers);
        }
    }

    private HttpResponse execute(HttpUriRequestBase request) throws HttpException {
        try {
            return httpClient.execute(request, response -> {
                HttpResponse result = new HttpResponse();
                result.setStatusCode(HttpStatus.valueOf(response.getCode()));

                HttpHeaders headersMap = new HttpHeaders();
                for (Header header : response.getHeaders()) {
                    // let the container handle Transfer-Encoding: chunked
                    if ("Transfer-Encoding".equalsIgnoreCase(header.getName())
                        && "chunked".equalsIgnoreCase(header.getValue())) {
                        continue;
                    }
                    headersMap.set(header.getName(), header.getValue());
                }
                result.setHeaders(headersMap);

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result.setBody(EntityUtils.toByteArray(entity));
                }
                return result;
            });
        } catch (IOException e) {
            throw new HttpException("Error while getting a response from " + request.getRequestUri()
                + ": " + e.getMessage(), e);
        } finally {
            request.reset();
        }
    }
}
