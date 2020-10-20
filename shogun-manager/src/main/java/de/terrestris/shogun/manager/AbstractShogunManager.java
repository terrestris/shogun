package de.terrestris.shogun.manager;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class as base for all manager subclasses
 */
@Log4j2
public abstract class AbstractShogunManager implements AutoCloseable {

    /**
     * The maximum number of connections allowed across all routes.
     */
    public static int MAX_NUMBER_CONNECTION = 200;

    /**
     * The maximum number of connections allowed for a route that has not been specified otherwise by a call to setMaxPerRoute
     */
    public static int MAX_DEFAULT_PER_ROUTE = 20;

    /**
     * The used referer for SHOGun manager
     */
    public final String SHOGUN_MANAGER_REFERER = "Shogun-Manager-Client";

    protected CloseableHttpClient httpClient;
    protected HttpClientContext context;
    protected ObjectMapper objectMapper;
    protected boolean closed = false;
    protected String shogunServiceBaseUrl;

    /**
     * Constructor: Initialize HTTP-client and contexts
     *
     * @param adminUser The admin user name (must have rule INTERCEPTOR_ADMIN)
     * @param adminPassword The password of admin
     * @param shogunBaseUrl The base URL of the target SHOGun microservice
     */
    public AbstractShogunManager(String adminUser, String adminPassword, String shogunBaseUrl) {
        // TODO : maybe this is needed in the future
//        SSLContext sslContext = null;
//        try {
//            sslContext = SSLContextBuilder
//                .create()
//                .loadTrustMaterial(new TrustSelfSignedStrategy())
//                .build();
//        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
//        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

        // Initialize connection pool
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(MAX_NUMBER_CONNECTION);
        cm.setDefaultMaxPerRoute(MAX_DEFAULT_PER_ROUTE);

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(adminUser, adminPassword));

        URI uri;
        try {
            uri = new URI(shogunBaseUrl);
        } catch (URISyntaxException e) {
           log.error("Could not parse SHOGun URL as URI. Since this is a required step, SHOGun manager will not be initialized");
           return;
        }

        // Apply basic auth
        HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort());
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        // Add AuthCache to the execution context
        context = HttpClientContext.create();
        context.setCredentialsProvider(credentialsProvider);
        context.setAuthCache(authCache);

        /*
         * Set referer for SHOGun manager
         */
        List<Header> defaultHeaders = new ArrayList<>();
        defaultHeaders.add(new BasicHeader(HttpHeaders.REFERER, SHOGUN_MANAGER_REFERER));

        httpClient = HttpClientBuilder
            .create()
            .setConnectionManager(cm)
            .setDefaultCredentialsProvider(credentialsProvider)
            .setDefaultHeaders(defaultHeaders)
//            .setSSLSocketFactory(connectionFactory) // TODO : maybe this is needed in the future
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JtsModule());

        this.shogunServiceBaseUrl = shogunBaseUrl;
    }

    /**
     * Perform HTTP request to REST endpoint
     * @param httpUriRequest THe {@link HttpUriRequest} to perform
     * @return A byte array containing the result
     * @throws IOException
     */
    byte[] performRequest(HttpUriRequest httpUriRequest) throws Exception {
        CloseableHttpResponse response = httpClient.execute(httpUriRequest, context);
        try {
            int statusCode = response.getStatusLine().getStatusCode();

            switch (statusCode) {
                case HttpStatus.SC_OK: return handleOkResult(response);
                case HttpStatus.SC_CREATED: return handleCreatedResult();
                case HttpStatus.SC_NO_CONTENT: return handleDeletedResult();
                case HttpStatus.SC_FORBIDDEN: return handleForbidden();
                case HttpStatus.SC_INTERNAL_SERVER_ERROR: throw new Exception("An error occurred while request to GeoServer interceptor.");
                default: return null;
            }
        } finally {
            response.close();
        }
    }

    /**
     * Handle DELETE result
     * @return byte array = [0]
     */
    private byte[] handleDeletedResult() {
        return new byte[]{0};
    }

    /**
     * handle 403 forbidden
     * @return null
     */
    private byte[] handleForbidden() {
        return null;
    }

    /**
     * Handle CREATE result
     * @return byte array = [1]
     */
    private byte[] handleCreatedResult() {
        return new byte[]{1};
    }

    /**
     * Handle HTTP OK result
     * @param response The {@link CloseableHttpClient} containing the response from SHOGun
     * @return byte array containing results
     */
    private byte[] handleOkResult(CloseableHttpResponse response) {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            HttpEntity iStream = response.getEntity();
            iStream.writeTo(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Could not parse result to resulting byte array: {}", e.getMessage());
            log.trace(e);
            return null;
        }
    }

    /**
     * Closes the underlying client.
     */
    @Override
    public void close() {
        closed = true;
        try {
            httpClient.close();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }

    /**
     * @return true if the underlying client is closed.
     */
    public boolean isClosed() {
        return closed;
    }
}
