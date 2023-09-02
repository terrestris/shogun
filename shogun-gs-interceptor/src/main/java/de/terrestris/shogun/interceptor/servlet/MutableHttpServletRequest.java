/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.interceptor.servlet;

import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.exception.InterceptorException;
import de.terrestris.shogun.interceptor.util.OgcXmlUtil;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * An implementation of HttpServletRequestWrapper.
 *
 * @author Daniel Koch
 * @see <a href="http://stackoverflow.com/questions/10210645/http-servlet-request-lose-params-from-post-body-after-read-it-once">
 * This stackoverflow discussion
 * </a>
 */
@Log4j2
public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    public static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * Holds custom parameter mapping
     */
    private Map<String, String[]> customParameters;

    /**
     * Holds custom header mapping
     */
    private Map<String, String> customHeaders;

    private String customRequestURI;

    private ByteArrayOutputStream cachedInputStream;

    private String method;

    private String queryString;

    /**
     * @param request
     */
    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.customRequestURI = request.getRequestURI();
        this.customParameters = new HashMap<>(request.getParameterMap());
        this.customHeaders = new HashMap<>();
    }

    /**
     * @param httpServletRequest
     * @param keys
     * @return
     * @throws InterceptorException
     * @throws IOException
     */
    public static String getRequestParameterValue(HttpServletRequest httpServletRequest, String[] keys) throws InterceptorException, IOException {
        String value = StringUtils.EMPTY;

        for (String key : keys) {
            value = getRequestParameterValue(httpServletRequest, key);
            if (StringUtils.isNotEmpty(value)) {
                break;
            }
        }

        return value;
    }

    @Override
    public String getMethod() {
        if (method != null) {
            return method;
        }
        return super.getMethod();
    }

    /**
     * Override the method. Can be used to convert a GET request to a POST request eg. when a GetMap-Request gets too
     * large.
     *
     * @param method the new method (GET, POST)
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Override what is returned by getQueryString.
     *
     * @param queryString the new query string
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    /**
     * @param httpServletRequest
     * @param parameter
     * @return
     */
    public static String getRequestParameterValue(HttpServletRequest httpServletRequest, String parameter) throws IOException, InterceptorException {
        log.trace("Finding the request parameter [" + parameter + "]");

        String value = StringUtils.EMPTY;
        Map<String, String[]> queryParams = httpServletRequest.getParameterMap();
        if (!queryParams.isEmpty()) {
            log.trace("The request contains query parameters (GET or POST).");

            Map<String, String[]> params = new TreeMap<>(
                String.CASE_INSENSITIVE_ORDER);

            params.putAll(queryParams);

            if (params.containsKey(parameter)) {
                value = StringUtils.join(params.get(parameter), ",");
            }
        } else {
            String xml = OgcXmlUtil.getRequestBody(httpServletRequest);
            if (!StringUtils.isEmpty(xml)) {
                log.trace("The request contains a POST body.");
                Document document = OgcXmlUtil.getDocumentFromString(xml);

                if (parameter.equalsIgnoreCase(OgcEnum.Service.SERVICE.toString())) {
                    value = OgcXmlUtil.getPathInDocument(document, "/*/@service");
                } else if (parameter.equalsIgnoreCase(OgcEnum.Operation.OPERATION.toString())) {
                    value = OgcXmlUtil.getPathInDocument(document, "name(/*)");
                    if (value.contains(":")) {
                        value = value.split(":")[1];
                    }
                } else if (Arrays.asList(OgcEnum.EndPoint.getAllValues()).contains(parameter)) {
                    value = OgcXmlUtil.getPathInDocument(document, "//TypeName/text() | //TypeNames/text() | //GetCoverage/Identifier/text()");
                    if (StringUtils.isEmpty(value)) {
                        value = OgcXmlUtil.getPathInDocument(document, "//@typeName | //@typeNames");
                    }
                }
            } else {
                log.error("No body found in the request.");
            }
        }

        log.trace("Found the request parameter value: " + value);
        return value;
    }

    /**
     *
     */
    @Override
    public String getRequestURI() {
        if (this.customRequestURI != null) {
            return this.customRequestURI;
        } else {
            HttpServletRequest request = (HttpServletRequest) super.getRequest();
            return request.getRequestURI();
        }
    }

    /**
     * @param url The URI to set as instance of {@link String}
     */
    public void setRequestURI(String url) {
        this.customRequestURI = url;
    }

    /**
     * @param uri The URI to set as instance of {@link URI}
     */
    public void setRequestURI(URI uri) {
        this.customRequestURI = uri.toString();
    }

    /**
     * @param key
     * @param value
     */
    public void setParameter(String key, String[] value) {
        if (!StringUtils.isEmpty(this.getParameter(key))) {
            this.removeParameter(key);
        }
        this.addParameter(key, value);
    }

    /**
     * @param key
     * @param value
     */
    public void setParameter(String key, String value) {
        if (!StringUtils.isEmpty(this.getParameter(key))) {
            this.removeParameter(key);
        }
        this.addParameter(key, value);
    }

    /**
     * @param key   The header name (without a trailing colon `:`)
     * @param value The header value
     */
    public void setHeader(String key, String value) {
        if (!StringUtils.isEmpty(this.getHeader(key))) {
            this.removeHeader(key);
        }
        customHeaders.put(key, value);
    }

    /**
     * @param key
     * @param value
     */
    public void addParameter(String key, String[] value) {
        customParameters.put(key, value);
    }

    /**
     * @param key
     * @param value
     */
    public void addParameter(String key, String value) {
        String[] values = value.split(",", -1);
        customParameters.put(key, values);
    }

    /**
     * @param key
     */
    public void removeParameter(String key) {
        if (customParameters.get(key) != null) {
            customParameters.remove(key);
        }
    }

    /**
     * @param key
     */
    public void removeHeader(String key) {
        if (customHeaders.get(key) != null) {
            customHeaders.remove(key);
        }
    }

    /**
     *
     */
    @Override
    public String getParameter(String key) {
        if (customParameters.containsKey(key)) {
            return StringUtils.join(customParameters.get(key), ",");
        } else {
            HttpServletRequest request = (HttpServletRequest) super.getRequest();
            return request.getParameter(key);
        }
    }

    /**
     *
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        return customParameters;
    }

    /**
     *
     */
    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);
        // Check custom headers first
        if (headerValue != null) {
            return headerValue;
        }
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    /**
     *
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedInputStream == null) {
            cacheInputStream();
        }
        return new CachedServletInputStream(cachedInputStream);
    }

    /**
     * Set the cachedInputStream as a copy of UTF-8 encoded {@link ByteArrayInputStream}
     *
     * @param body {@link String} body to create the {@link ByteArrayInputStream} from
     */
    public void setInputStream(String body) {
        try (
            ByteArrayInputStream stream = new ByteArrayInputStream(body.getBytes(DEFAULT_CHARSET))
        ) {
            cachedInputStream = new ByteArrayOutputStream();
            IOUtils.copy(stream, cachedInputStream);
        } catch (IOException e) {
            log.error("Exception on writing InputStream.", e);
        }
    }

    /**
     * Set the cachedInputStream as a copy of passed {@link InputStream}
     *
     * @param stream The {@link InputStream} to set (copy)
     */
    public void setInputStream(InputStream stream) {
        try {
            cachedInputStream = new ByteArrayOutputStream();
            IOUtils.copy(stream, cachedInputStream);
        } catch (IOException e) {
            log.error("Exception on writing InputStream.", e);
        }
    }

    /**
     *
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * Cache the inputstream in order to read it multiple times. For
     * convenience, I use apache.commons IOUtils
     */
    private void cacheInputStream() throws IOException {
        cachedInputStream = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), cachedInputStream);
    }

    /**
     * Get a parameter by name, ignoring case.
     *
     * @param name the parameter to get
     * @return a comma separated list of parameter values
     */
    public String getParameterIgnoreCase(String name) {
        for (Map.Entry<String, String[]> entry : customParameters.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return StringUtils.join(entry.getValue(), ",");
            }
        }
        return null;
    }

}
