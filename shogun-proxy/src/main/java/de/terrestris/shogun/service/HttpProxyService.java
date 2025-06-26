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

import de.terrestris.shogun.config.properties.HttpProxyProperties;
import de.terrestris.shogun.lib.dto.HttpResponse;
import de.terrestris.shogun.lib.util.HttpUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple HTTP Proxy service (forward proxy)
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
@Service("httpProxyService")
@Log4j2
public class HttpProxyService {

    /* +--------------------------------------------------------------------+ */
    /* | static errors and response entities                                | */
    /* +--------------------------------------------------------------------+ */
    public static final String ERR_MSG_400_NO_URL = "ERROR 400 (Bad Request):"
        + " The HttpProxyService could not determine a URL to proxy to.";

    /* +--------------------------------------------------------------------+ */
    /* | Generic constants                                                  | */
    /* +--------------------------------------------------------------------+ */
    public static final String ERR_MSG_400_COMMON = "ERROR 400 (Bad Request):"
        + " Please check the log files for details.";
    public static final String ERR_MSG_405 = "ERROR 405: (Method Not Allowed):"
        + " The HttpProxyService does not support this request method.";
    public static final String ERR_MSG_500 = "ERROR 500 (Internal Error)"
        + " An internal error occurred which prevented further processing.";
    public static final String ERR_MSG_502 = "ERROR 502 (Bad Gateway):"
        + " The HttpProxyService does not allow you to access that location.";

    /**
     * Used to as content type for error messages if a request could not be
     * proxied.
     */
    private static final String CONTENT_TYPE_TEXT_PLAIN = MediaType.TEXT_PLAIN.toString();
    private static final ResponseEntity<String> RESPONSE_400_BAD_REQUEST_COMMON =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_400_COMMON);

    private static final ResponseEntity<String> RESPONSE_400_BAD_REQUEST_NO_URL =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_400_NO_URL);

    private static final ResponseEntity<String> RESPONSE_405_METHOD_NOT_ALLOWED =
        ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_405);

    private static final ResponseEntity<String> RESPONSE_500_INTERNAL_SERVER_ERROR =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_500);

    private static final ResponseEntity<String> RESPONSE_502_BAD_GATEWAY =
        ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_502);

    /* +--------------------------------------------------------------------+ */
    /* | HTTP header stuff                                                  | */
    /* +--------------------------------------------------------------------+ */
    /**
     * The ports for http / https connections
     */
    private static final int HTTPS_PORT = 443;
    private static final int HTTP_PORT = 80;

    @Autowired
    protected HttpProxyProperties httpProxyProperties;

    /**
     * Proxy {@link HttpServletRequest} given the base URL and its params
     * @param request The {@link HttpServletRequest} to proxy
     * @param baseUrl The base url of the service
     * @param params The Request params
     * @return The {@link ResponseEntity}
     */
    public ResponseEntity<?> doProxy(HttpServletRequest request, String baseUrl, Map<String, String> params) {
        return doProxy(request, baseUrl, params, true);
    }

    /**
     * Proxy {@link HttpServletRequest} given the base URL and its params
     * @param request The {@link HttpServletRequest} to proxy
     * @param baseUrl The base url of the service
     * @param params The Request params
     * @param useWhitelist Check if host / server is listed in whitelist
     * @return The {@link ResponseEntity}
     */
    public ResponseEntity<?> doProxy(HttpServletRequest request, String baseUrl, Map<String, String> params, boolean useWhitelist) {
        log.debug("Intercepting a request against service '" + baseUrl + "' with parameters: " + params);

        if (StringUtils.isEmpty(baseUrl) || request == null) {
            log.warn(ERR_MSG_400_NO_URL);
            return RESPONSE_400_BAD_REQUEST_NO_URL;
        }

        // transform to URL
        URL url;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException use) {
            log.error(RESPONSE_500_INTERNAL_SERVER_ERROR);
            log.trace(RESPONSE_500_INTERNAL_SERVER_ERROR, use);
            return RESPONSE_500_INTERNAL_SERVER_ERROR;
        }

        if (useWhitelist) {
            // check if URI is contained in whitelist
            final boolean isInWhiteList = isInWhiteList(url);

            if (!isInWhiteList) {
                log.warn(ERR_MSG_502);
                return RESPONSE_502_BAD_GATEWAY;
            }
        }

        // build request for params and baseUrl;
        try {
            url = buildUriWithParameters(url, params);
        } catch (URISyntaxException | MalformedURLException excep) {
            log.error(RESPONSE_500_INTERNAL_SERVER_ERROR);
            log.trace(RESPONSE_500_INTERNAL_SERVER_ERROR, excep);
            return RESPONSE_500_INTERNAL_SERVER_ERROR;
        }

        // Proxy the request
        HttpResponse response;
        if (HttpUtil.isHttpGetRequest(request)) {
            try {
                log.debug("Forwarding as GET to: " + url);
                response = HttpUtil.forwardGet(url.toURI(), request, false);
            } catch (URISyntaxException | HttpException e) {
                String errorMessage = String.format("Error forwarding GET request: %s", e.getMessage());
                log.error(errorMessage);
                log.trace(errorMessage, e);
                return RESPONSE_400_BAD_REQUEST_COMMON;
            }
        } else if (HttpUtil.isHttpPostRequest(request)) {
            if (HttpUtil.isFormMultipartPost(request)) {
                try {
                    log.debug("Forwarding as form/multipart POST");
                    response = HttpUtil.forwardFormMultipartPost(url.toURI(), request, false);
                } catch (URISyntaxException | HttpException | IllegalStateException | IOException | ServletException e) {
                    String errorMessage = String.format("Error forwarding form/multipart POST request: %s", e.getMessage());
                    log.error(errorMessage);
                    log.trace(errorMessage, e);
                    return RESPONSE_400_BAD_REQUEST_COMMON;
                }
            } else {
                try {
                    log.debug("Forwarding as POST");
                    response = HttpUtil.forwardPost(url.toURI(), request, false);
                } catch (URISyntaxException | HttpException e) {
                    String errorMessage = "Error forwarding POST request: " + e.getMessage();
                    log.error(errorMessage);
                    log.trace(errorMessage, e);
                    return RESPONSE_400_BAD_REQUEST_COMMON;
                }
            }
        } else {
            log.error("Proxy does not support HTTP method: " + request.getMethod());
            return RESPONSE_405_METHOD_NOT_ALLOWED;
        }

        byte[] bytes = response.getBody();
        final HttpHeaders responseHeadersToForward = response.getHeaders();

        // LOG response headers
        Set<Map.Entry<String, List<String>>> headerEntries = responseHeadersToForward.entrySet();
        for (Map.Entry<String, List<String>> headerEntry : headerEntries) {
            String headerKey = headerEntry.getKey();
            List<String> headerValues = headerEntry.getValue();
            String joinedHeaderValues = StringUtils.join(headerValues, "; ");

            log.debug("Got the following response header: " + headerKey + "=" + joinedHeaderValues);
        }

        final HttpStatus responseHttpStatus = response.getStatusCode();
        return new ResponseEntity<>(bytes, responseHeadersToForward, responseHttpStatus);
    }

    /**
     * Helper method to build an {@link URL} from a baseUri and request parameters
     *
     * @param url    Base {@link URL}
     * @param params request parameters
     * @return URI
     */
    private URL buildUriWithParameters(URL url, Map<String, String> params) throws URISyntaxException, MalformedURLException {
        if (params == null || params.isEmpty()) {
            return url;
        }
        URIBuilder uriBuilder = new URIBuilder(url.toURI());

        params.forEach((key, value) -> {
            if (!StringUtils.equalsIgnoreCase(key, "baseUrl")) {
                uriBuilder.addParameter(key, value);
            } else {
                log.warn("Skipping baseUrl empty parameter: baseUrl ={}", value);
            }
        });

        return uriBuilder.build().toURL();
    }

    /**
     * Helper method to check whether the URI is contained in the host whitelist provided in list of whitelisted hosts
     *
     * @param url {@link java.net.URI} to check
     * @return true if contained, false otherwise
     */
    private boolean isInWhiteList(URL url) {
        final String host = url.getHost();
        final int port = url.getPort();
        final String protocol = url.getProtocol();

        int portToTest;
        if (port != -1) {
            portToTest = port;
        } else {
            portToTest = StringUtils.equalsIgnoreCase(protocol, "https") ? HTTPS_PORT : HTTP_PORT;
        }

        final int finalPortToTest = portToTest;
        var proxyWhiteList = httpProxyProperties.getWhitelist();
        return proxyWhiteList.stream().anyMatch(whitelistEntry -> {
            String whitelistHost;
            int whitelistPort;
            if (StringUtils.contains(whitelistEntry, ":")) {
                whitelistHost = whitelistEntry.split(":")[0];
                whitelistPort = Integer.parseInt(whitelistEntry.split(":")[1]);
            } else {
                whitelistHost = whitelistEntry;
                whitelistPort = -1;
            }
            final int portToTestAgainst = (whitelistPort != -1) ? whitelistPort : (StringUtils.equalsIgnoreCase(protocol, "https") ? HTTPS_PORT : HTTP_PORT);
            final boolean portIsMatching = portToTestAgainst == finalPortToTest;
            final boolean domainIsMatching = StringUtils.equalsIgnoreCase(host, whitelistHost) || StringUtils.endsWith(host, whitelistHost);
            return (portIsMatching && domainIsMatching);
        });
    }

}
