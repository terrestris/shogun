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
import lombok.extern.log4j.Log4j2;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Stateless helpers for inspecting an {@link HttpServletRequest}.
 */
@Log4j2
public final class HttpRequestUtil {

    private HttpRequestUtil() {
    }

    /**
     * Returns the full web application URI from a given request.
     * <p>
     * Example: a request to {@code http://localhost:8080/shogun/user/resetPassword.action}
     * results in {@code http://localhost:8080/shogun}.
     *
     * @param request The request.
     * @return The application URI.
     * @throws URISyntaxException If the URI could not be built.
     */
    public static URI getApplicationURIFromRequest(HttpServletRequest request) throws URISyntaxException {
        return new URIBuilder()
            .setScheme(request.getScheme())
            .setHost(request.getServerName())
            .setPort(request.getServerPort())
            .setPath(request.getContextPath())
            .build();
    }

    /**
     * Checks if request and request method are not null.
     *
     * @param request {@link HttpServletRequest} to check
     * @return true if sane, false otherwise
     */
    public static boolean isSaneRequest(HttpServletRequest request) {
        return request != null && request.getMethod() != null;
    }

    /**
     * Check if provided {@link HttpServletRequest} is an HTTP GET request.
     *
     * @param request {@link HttpServletRequest} to check
     * @return true if HTTP GET, false otherwise
     */
    public static boolean isHttpGetRequest(HttpServletRequest request) {
        return isSaneRequest(request) && HttpMethod.GET.name().equals(request.getMethod());
    }

    /**
     * Check if provided {@link HttpServletRequest} is an HTTP POST request.
     *
     * @param request {@link HttpServletRequest} to check
     * @return true if HTTP POST, false otherwise
     */
    public static boolean isHttpPostRequest(HttpServletRequest request) {
        return isSaneRequest(request) && HttpMethod.POST.name().equals(request.getMethod());
    }

    /**
     * Check if provided {@link HttpServletRequest} is a form/multipart POST request.
     *
     * @param request {@link HttpServletRequest} to check
     * @return true if form/multipart POST, false otherwise
     */
    public static boolean isFormMultipartPost(HttpServletRequest request) {
        if (!isHttpPostRequest(request)) {
            return false;
        }

        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/form-data");
    }

    /**
     * Extract headers from {@link HttpServletRequest}. Headers with an empty value are skipped.
     *
     * @param request {@link HttpServletRequest} to extract headers from
     * @return Array with {@link Header}s
     */
    public static Header[] getHeadersFromRequest(HttpServletRequest request) {
        List<Header> headers = new ArrayList<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();

                if (headerValue.isEmpty()) {
                    log.debug("Skipping request header '{}' as its value is empty", headerName);
                } else {
                    log.debug("Adding request header: {}={}", headerName, headerValue);
                    headers.add(new BasicHeader(headerName, headerValue));
                }
            }
        }

        return headers.toArray(new Header[0]);
    }

}
