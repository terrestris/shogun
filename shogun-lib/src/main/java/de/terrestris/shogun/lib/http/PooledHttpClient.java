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
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.ByteArrayBody;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A shared, pooled {@link CloseableHttpClient} exposed as a Spring bean. It replaces the former static
 * {@code HttpUtil}.
 *
 * <p>This enables:
 * <ul>
 *   <li>Connection reuse / HTTP Keep-Alive</li>
 *   <li>A bounded pool of sockets (no unbounded resource growth)</li>
 *   <li>Fewer TCP handshakes / TLS negotiations, i.e. lower latency</li>
 *   <li>No per-request allocation of connection managers etc.</li>
 * </ul>
 *
 * <p>Authentication (Basic auth) and the system proxy (JVM properties, environment variables,
 * {@code NO_PROXY}, {@link java.net.ProxySelector}) are resolved per request, so they do not affect the
 * shared client.
 *
 * <p><b>Usage:</b>
 * <pre>{@code
 * private final PooledHttpClient pooledHttpClient;
 *
 * public MyService(PooledHttpClient pooledHttpClient) {
 *     this.pooledHttpClient = pooledHttpClient;
 * }
 *
 * HttpResponse resp = pooledHttpClient.get(uri, headers);
 * }</pre>
 */
@Component
@Log4j2
public class PooledHttpClient {

    /**
     * The HTTP timeout in milliseconds. Default is 15000 ms (15 seconds), 0 means no timeout.
     */
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
    private RequestConfig defaultRequestConfig;

    @PostConstruct
    public void init() {
        if (httpTimeout < 0) {
            throw new IllegalArgumentException("Timeout value cannot be negative");
        }

        if (httpTimeout == 0) {
            log.warn("Configuring PooledHttpClient with timeout set to 0 ms. This means no timeout!");
        }

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setConnectTimeout(Timeout.ofMilliseconds(httpTimeout))
            .build();

        connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setDefaultConnectionConfig(connectionConfig)
            .setMaxConnTotal(maxTotal)
            .setMaxConnPerRoute(maxPerRoute)
            .build();

        defaultRequestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(httpTimeout))
            .setResponseTimeout(Timeout.ofMilliseconds(httpTimeout))
            .build();

        httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(defaultRequestConfig)
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
        return get(uri, null, requestHeaders);
    }

    /**
     * Perform a GET request. Basic auth is used if the credentials are not null.
     */
    public HttpResponse get(URI uri, Credentials credentials, Header[] requestHeaders) throws HttpException {
        return execute(new HttpGet(uri), credentials, requestHeaders);
    }

    /**
     * Forward a GET to the given URI based on the given request.
     *
     * @param uri            The URI to forward to.
     * @param request        The original {@link HttpServletRequest}
     * @param forwardHeaders Whether the headers of the request should be forwarded
     */
    public HttpResponse forwardGet(URI uri, HttpServletRequest request, boolean forwardHeaders)
        throws HttpException {
        Header[] headersToForward = null;

        if (request != null && forwardHeaders) {
            headersToForward = HttpRequestUtil.getHeadersFromRequest(request);
        }

        return get(uri, headersToForward);
    }

    /**
     * Perform a POST request with a raw string body (e.g. XML for WFS-T).
     */
    public HttpResponse post(URI uri, String body, ContentType contentType, Header[] requestHeaders)
        throws HttpException {
        return post(uri, body, contentType, null, requestHeaders, false);
    }

    /**
     * Perform a POST request with a raw string body (e.g. XML for WFS-T).
     *
     * @param chunked Whether the body should be sent chunked
     */
    public HttpResponse post(URI uri, String body, ContentType contentType, Header[] requestHeaders,
                             boolean chunked) throws HttpException {
        return post(uri, body, contentType, null, requestHeaders, chunked);
    }

    /**
     * Perform a POST request with a raw string body. Basic auth is used if the credentials are not null.
     */
    public HttpResponse post(URI uri, String body, ContentType contentType, Credentials credentials,
                             Header[] requestHeaders, boolean chunked) throws HttpException {
        HttpPost request = new HttpPost(uri);
        if (body != null) {
            request.setEntity(new StringEntity(body, contentType, chunked));
        }
        return execute(request, credentials, requestHeaders);
    }

    /**
     * Perform a POST request with form parameters (application/x-www-form-urlencoded, UTF-8).
     */
    public HttpResponse post(URI uri, List<NameValuePair> params, Header[] requestHeaders)
        throws HttpException {
        return post(uri, params, null, requestHeaders);
    }

    /**
     * Perform a POST request with form parameters (application/x-www-form-urlencoded, UTF-8).
     * Basic auth is used if the credentials are not null.
     */
    public HttpResponse post(URI uri, List<NameValuePair> params, Credentials credentials,
                             Header[] requestHeaders) throws HttpException {
        HttpPost request = new HttpPost(uri);
        if (params != null && !params.isEmpty()) {
            request.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        }
        return execute(request, credentials, requestHeaders);
    }

    /**
     * Perform a multipart POST request with the given file as part named {@code file}.
     * Basic auth is used if the credentials are not null.
     */
    public HttpResponse post(URI uri, File file, Credentials credentials, Header[] requestHeaders)
        throws HttpException {
        HttpPost request = new HttpPost(uri);
        request.setEntity(MultipartEntityBuilder.create()
            .addPart("file", new FileBody(file))
            .build());
        return execute(request, credentials, requestHeaders);
    }

    /**
     * Forward a POST to the given URI based on the given request.
     *
     * @param uri            The URI to forward to.
     * @param request        The original {@link HttpServletRequest}
     * @param forwardHeaders Whether the headers of the request should be forwarded
     */
    public HttpResponse forwardPost(URI uri, HttpServletRequest request, boolean forwardHeaders)
        throws HttpException {
        return forwardPost(uri, request, forwardHeaders, false);
    }

    /**
     * Forward a POST to the given URI based on the given request.
     *
     * @param uri            The URI to forward to.
     * @param request        The original {@link HttpServletRequest}
     * @param forwardHeaders Whether the headers of the request should be forwarded
     * @param chunked        Whether the body should be sent chunked
     */
    public HttpResponse forwardPost(URI uri, HttpServletRequest request, boolean forwardHeaders,
                                    boolean chunked) throws HttpException {
        if (request == null) {
            throw new HttpException("Cannot forward a POST without a request");
        }

        Header[] headersToForward = forwardHeaders ? HttpRequestUtil.getHeadersFromRequest(request) : null;
        ContentType contentType = ContentType.parse(request.getContentType());
        String body = getRequestBody(request);

        return post(uri, body, contentType, headersToForward, chunked);
    }

    /**
     * Forward a form/multipart POST to the given URI based on the given request.
     *
     * @param uri            The URI to forward to.
     * @param request        The original {@link HttpServletRequest}
     * @param forwardHeaders Whether the headers of the request should be forwarded
     */
    public HttpResponse forwardFormMultipartPost(URI uri, HttpServletRequest request, boolean forwardHeaders)
        throws HttpException, IOException, ServletException {
        if (request == null) {
            throw new HttpException("Cannot forward a multipart POST without a request");
        }

        Header[] headersToForward = forwardHeaders ? HttpRequestUtil.getHeadersFromRequest(request) : null;

        // content headers are removed as the client lib sets them when the entity is set on the request
        headersToForward = removeHeaders(headersToForward, "content-length", "content-type");

        return postMultiPart(uri, request.getParts(), headersToForward);
    }

    private HttpResponse postMultiPart(URI uri, Collection<Part> parts, Header[] requestHeaders)
        throws IOException, HttpException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for (Part part : parts) {
            String name = part.getName();

            // ByteArrayBody instead of InputStreamBody: the latter would result in chunked encoding,
            // which is not supported by all servers. The known size results in a content-length header.
            byte[] data = IOUtils.toByteArray(part.getInputStream());
            ContentType contentType = ContentType.create(part.getContentType());

            builder.addPart(name, new ByteArrayBody(data, contentType, part.getSubmittedFileName()));

            log.debug("Add a form/multipart part with name '{}', content type '{}' and size {}",
                name, contentType.getMimeType(), part.getSize());
        }

        HttpPost request = new HttpPost(uri);
        request.setEntity(builder.build());
        return execute(request, null, requestHeaders);
    }

    /**
     * Perform a PUT request with an optional string body. The body is only sent if the content type is
     * not null and the body is not empty.
     */
    public HttpResponse put(URI uri, String body, ContentType contentType, Header[] requestHeaders,
                            boolean chunked) throws HttpException {
        return put(uri, body, contentType, null, requestHeaders, chunked);
    }

    /**
     * Perform a PUT request with an optional string body. The body is only sent if the content type is
     * not null and the body is not empty. Basic auth is used if the credentials are not null.
     */
    public HttpResponse put(URI uri, String body, ContentType contentType, Credentials credentials,
                            Header[] requestHeaders, boolean chunked) throws HttpException {
        HttpPut request = new HttpPut(uri);
        if (contentType != null && !StringUtils.isEmpty(body)) {
            request.setEntity(new StringEntity(body, contentType, chunked));
        }
        return execute(request, credentials, requestHeaders);
    }

    /**
     * Perform a DELETE request.
     */
    public HttpResponse delete(URI uri, Header[] requestHeaders) throws HttpException {
        return delete(uri, null, requestHeaders);
    }

    /**
     * Perform a DELETE request. Basic auth is used if the credentials are not null.
     */
    public HttpResponse delete(URI uri, Credentials credentials, Header[] requestHeaders)
        throws HttpException {
        return execute(new HttpDelete(uri), credentials, requestHeaders);
    }

    // internals

    private HttpResponse execute(HttpUriRequestBase request, Credentials credentials, Header[] requestHeaders)
        throws HttpException {
        if (requestHeaders != null) {
            request.setHeaders(requestHeaders);
        }

        HttpClientContext context = HttpClientContext.create();

        try {
            URI uri = request.getUri();
            configureProxy(request, context, uri);
            configureAuthentication(context, credentials, uri);
        } catch (URISyntaxException e) {
            throw new HttpException("Invalid request URI: " + e.getMessage(), e);
        }

        try {
            log.debug("Executing request {} with headers {}", request.getRequestUri(), request.getHeaders());
            return httpClient.execute(request, context, response -> {
                HttpResponse result = new HttpResponse();
                result.setStatusCode(HttpStatus.valueOf(response.getCode()));
                log.debug("Received response with status code {} and headers {}", result.getStatusCode(), response.getHeaders());
                HttpHeaders headersMap = new HttpHeaders();
                for (Header header : response.getHeaders()) {
                    // let the container handle Transfer-Encoding: chunked
                    if ("Transfer-Encoding".equalsIgnoreCase(header.getName())
                        && "chunked".equalsIgnoreCase(header.getValue())) {
                        log.trace("Removed the header 'Transfer-Encoding:chunked' from a response, "
                            + "as it's handled by the http-client");
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

    private void configureProxy(HttpUriRequestBase request, HttpClientContext context, URI uri) {
        HttpHost proxy;
        try {
            proxy = SystemProxyResolver.resolve(uri);
        } catch (UnknownHostException e) {
            log.error("Error while detecting system wide proxy: {}", e.getMessage());
            return;
        }

        if (proxy == null) {
            return;
        }

        log.debug("Using proxy {}:{}", proxy.getHostName(), proxy.getPort());

        request.setConfig(RequestConfig.copy(defaultRequestConfig).setProxy(proxy).build());

        UsernamePasswordCredentials proxyCredentials = SystemProxyResolver.proxyCredentials(uri);
        if (proxyCredentials != null) {
            getCredentialsProvider(context).setCredentials(
                new AuthScope(proxy.getHostName(), proxy.getPort()),
                proxyCredentials
            );
        }
    }

    private void configureAuthentication(HttpClientContext context, Credentials credentials, URI uri) {
        if (credentials == null) {
            return;
        }

        getCredentialsProvider(context).setCredentials(new AuthScope(uri.getHost(), uri.getPort()), credentials);

        // preemptive basic authentication
        BasicScheme basicScheme = new BasicScheme();
        basicScheme.initPreemptive(credentials);

        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(new HttpHost(uri.getScheme(), uri.getHost(), uri.getPort()), basicScheme);
        context.setAuthCache(authCache);
    }

    private BasicCredentialsProvider getCredentialsProvider(HttpClientContext context) {
        if (context.getCredentialsProvider() instanceof BasicCredentialsProvider provider) {
            return provider;
        }
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        context.setCredentialsProvider(provider);
        return provider;
    }

    private static Header[] removeHeaders(Header[] headersToClean, String... headersToRemove) {
        if (headersToClean == null) {
            return null;
        }

        List<Header> headers = new ArrayList<>();
        for (Header header : headersToClean) {
            if (!Strings.CI.equalsAny(header.getName(), headersToRemove)) {
                headers.add(header);
            }
        }
        return headers.toArray(new Header[0]);
    }

    private static String getRequestBody(HttpServletRequest request) {
        try (BufferedReader requestReader = request.getReader()) {
            StringBuilder bodyLines = new StringBuilder();
            String bodyLine;
            while ((bodyLine = requestReader.readLine()) != null) {
                bodyLines.append(bodyLine);
            }
            return bodyLines.toString();
        } catch (IOException e) {
            log.info("Failed to obtain a Reader for a potential body of this POST, assuming KVP");
            return null;
        }
    }
}
