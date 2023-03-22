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
package de.terrestris.shogun.interceptor.service;

import de.terrestris.shogun.interceptor.config.properties.InterceptorProperties;
import de.terrestris.shogun.interceptor.config.properties.NamespaceProperties;
import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.interceptor.exception.InterceptorException;
import de.terrestris.shogun.interceptor.message.OgcMessage;
import de.terrestris.shogun.interceptor.message.OgcMessageDistributor;
import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import de.terrestris.shogun.interceptor.util.OgcXmlUtil;
import de.terrestris.shogun.lib.dto.HttpResponse;
import de.terrestris.shogun.lib.util.HttpUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class GeoServerInterceptorService {

    /**
     * An array of whitelisted Headers to forward within the Interceptor.
     */
    private static final String[] FORWARD_RESPONSE_HEADER_KEYS = new String[]{
        "Content-Type",
        "Content-Disposition",
        "Content-Language",
        "geowebcache-cache-result",
        "geowebcache-crs",
        "geowebcache-gridset",
        "geowebcache-tile-bounds",
        "geowebcache-tile-index",
        "geowebcache-miss-reason"
    };

    private static final String WMS_REFLECT_ENDPOINT = "/reflect";
    private static final String USE_REFLECT_PARAM = "useReflect";

    @Autowired
    protected OgcMessageDistributor ogcMessageDistributor;

    @Autowired
    protected InterceptorRuleService interceptorRuleService;

    @Autowired
    protected InterceptorProperties interceptorProperties;

    /**
     * @param params
     * @return
     */
    private static List<NameValuePair> createQueryParams(Map<String, String[]> params) {
        List<NameValuePair> queryParams = new ArrayList<>();
        for (Entry<String, String[]> param : params.entrySet()) {
            queryParams.add(new BasicNameValuePair(param.getKey(),
                StringUtils.join(param.getValue(), ",")));
        }
        return queryParams;
    }

    /**
     * @param baseUri
     * @param queryParams
     * @return
     * @throws URISyntaxException
     */
    private static URI getFullRequestURI(URI baseUri, List<NameValuePair> queryParams) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(baseUri);
        builder.addParameters(queryParams);
        return builder.build();
    }

    /**
     * @param endPoint
     */
    private static String getGeoServerNameSpace(String endPoint) {
        // return the endPoint as nameSpace per default
        String geoServerNamespace = endPoint;

        if (endPoint.contains(":")) {
            String[] split = endPoint.split(":");
            geoServerNamespace = split[0];
        }

        return geoServerNamespace;
    }

    /**
     * @param request
     * @throws InterceptorException
     * @throws HttpException
     */
    public static HttpResponse sendRequest(MutableHttpServletRequest request) throws InterceptorException, HttpException {
        HttpResponse httpResponse = new HttpResponse();
        String requestMethod = request.getMethod();
        boolean getRequest = "GET".equalsIgnoreCase(requestMethod);
        boolean postRequest = "POST".equalsIgnoreCase(requestMethod);

        try {
            // get the request URI
            URI requestUri = new URI(request.getRequestURI());

            Header[] requestHeaders = getRequestHeadersToForward(request);

            // get the query parameters provided by the GET/POST request and
            // convert to a list of NameValuePairs
            List<NameValuePair> allQueryParams = createQueryParams(request.getParameterMap());

            // append the given request parameters to the base URI
            URI fullRequestUri = getFullRequestURI(requestUri, allQueryParams);

            if (getRequest) {
                // if we're called via GET method
                // perform the request with the given parameters
                httpResponse = HttpUtil.get(fullRequestUri, requestHeaders);

            } else if (postRequest) {
                // if we're called via POST method

                // We have to attach the actual query; a POST to e.g. http://example.com/?foo=bar is totally OK
                String queryString = request.getQueryString();
                if (queryString != null) {
                    requestUri = appendQueryString(requestUri, queryString);
                }

                // get the request body if any
                String body = OgcXmlUtil.getRequestBody(request);
                if (!StringUtils.isEmpty(body)) {
                    // we do have a POST with string data present
                    // parse the content type of the request
                    ContentType contentType = ContentType.parse(request.getContentType());

                    if (contentType.getCharset() == null) {
                        // use UTF-8 charset if charset could not be parsed from the content type
                        // of the request
                        contentType = contentType.withCharset("UTF-8");
                    }

                    // perform the POST request to the URI with queryString and with the given body
                    httpResponse = HttpUtil.post(requestUri, body, contentType, requestHeaders, false);
                } else {
                    // perform the POST request with the given name value pairs,
                    httpResponse = HttpUtil.post(requestUri, allQueryParams, requestHeaders);
                }

            } else {
                // otherwise throw an exception
                throw new InterceptorException("Only GET or POST method is allowed");
            }

        } catch (URISyntaxException | UnsupportedEncodingException e) {
            log.error("Error while sending request: " + e.getMessage());
        }

        return httpResponse;
    }

    /**
     * @param request
     * @return
     */
    private static Header[] getRequestHeadersToForward(MutableHttpServletRequest request) {
        List<Header> requestHeaderList = new ArrayList<>();

        // forward x-geoserver-credentials as Authorization header if available
        String credentials = request.getHeader("x-geoserver-credentials");
        if (credentials != null) {
            requestHeaderList.add(new BasicHeader(HttpHeaders.AUTHORIZATION, credentials));
        }

        return requestHeaderList.toArray(new Header[0]);
    }

    /**
     * Returns a new URI with the passed queryString (e.g. foo=bar&amp;baz=123) appended to the passed URI. Adjusted from
     * http://stackoverflow.com/a/26177982.
     *
     * @param uri
     * @param appendQuery
     */
    public static URI appendQueryString(URI uri, String appendQuery) {
        if (uri == null || appendQuery == null || appendQuery.isEmpty()) {
            return uri;
        }
        String newQuery = uri.getQuery();
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;
        }
        // Fallback is the old URI
        URI newUri = uri;
        try {
            newUri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), newQuery, uri.getFragment());
        } catch (URISyntaxException e) {
            String msg = String.format(
                "Failed to append query '%s' to URI '%s', returning URI unchanged.",
                appendQuery, uri
            );
            log.warn(msg);
        }
        return newUri;
    }

    /**
     * @return
     * @throws UnsupportedEncodingException
     */
    private static HttpHeaders getResponseHeadersToForward(HttpHeaders headers) {
        HttpHeaders responseHeaders = new HttpHeaders();

        if (headers == null) {
            log.debug("No headers found to forward!");
            return responseHeaders;
        }

        log.trace("Requested to filter the Headers to respond with:");

        for (Entry<String, List<String>> header : headers.entrySet()) {
            String headerKey = header.getKey();
            String headerVal = StringUtils.join(header.getValue(), ",");

            log.trace("  * Header: " + headerKey);

            if (Arrays.asList(FORWARD_RESPONSE_HEADER_KEYS).contains(headerKey)) {
                // the GeoServer response may contain a subtype in the
                // "Content-Type" header without double quotes surrounding the
                // subtype's value. If this is set we need to surround it
                // with double quotes as this is required by the Spring
                // ResponseEntity (and the RFC 2616 standard).
                Pattern pattern = Pattern.compile("subtype=(.*)");
                Matcher matcher = pattern.matcher(headerVal);

                if (matcher.find()) {
                    String replaceCandidate = matcher.group(1);
                    String replacer;

                    replacer = StringUtils.prependIfMissing(
                        replaceCandidate, "\"");
                    replacer = StringUtils.appendIfMissing(
                        replacer, "\"");

                    headerVal = StringUtils.replace(headerVal,
                        replaceCandidate, replacer);
                }

                responseHeaders.set(headerKey, headerVal);
                log.trace("    > Forwarded");
            } else {
                log.trace("    > Skipped");
            }
        }

        return responseHeaders;
    }

    /**
     * @param request
     * @return
     * @throws InterceptorException
     * @throws URISyntaxException
     * @throws HttpException
     * @throws IOException
     */
    public HttpResponse interceptGeoServerRequest(HttpServletRequest request) throws InterceptorException, URISyntaxException, HttpException, IOException {
        return interceptGeoServerRequest(request, Optional.empty());
    }

    /**
     * @param request
     * @param endpoint
     * @return
     * @throws InterceptorException
     * @throws URISyntaxException
     * @throws HttpException
     * @throws IOException
     */
    public HttpResponse interceptGeoServerRequest(HttpServletRequest request, Optional<String> endpoint) throws InterceptorException, URISyntaxException, HttpException, IOException {
        // wrap the request, we want to manipulate it
        MutableHttpServletRequest mutableRequest =
            new MutableHttpServletRequest(request);
        if (endpoint.isPresent()) {
            mutableRequest.addParameter("CUSTOM_ENDPOINT", endpoint.get());
            mutableRequest.addParameter("CONTEXT_PATH", request.getContextPath());
        }

        // get the OGC message information (service, request, endPoint)
        OgcMessage message = getOgcMessage(mutableRequest);

        // check whether WMS reflector endpoint should be called
        final boolean useWmsReflector = shouldReflectEndpointBeCalled(mutableRequest, message);

        // get the GeoServer base URI by the provided request
        URI geoServerBaseUri = getGeoServerBaseURI(message, useWmsReflector);

        // set the GeoServer base URI to the (wrapped) request
        mutableRequest.setRequestURI(geoServerBaseUri);

        // intercept the request (if needed)
        mutableRequest = ogcMessageDistributor
            .distributeToRequestInterceptor(mutableRequest, message);

        // send the request
        // TODO: Move to global proxy class
        HttpResponse response = sendRequest(mutableRequest);

        // intercept the response (if needed)
        HttpResponse interceptedResponse = ogcMessageDistributor
            .distributeToResponseInterceptor(mutableRequest, response, message);

        // finally filter the white-listed response headers
        // TODO: Move to global proxy class
        HttpHeaders forwardingHeaders = getResponseHeadersToForward(
            interceptedResponse.getHeaders()
        );
        interceptedResponse.setHeaders(forwardingHeaders);

        return interceptedResponse;
    }

    /**
     * Detect whether the WMS reflector endpoint of GeoServer should be called instead of the one defined in provided message
     *
     * @param mutableRequest request to check
     * @param message        instance of {@link OgcMessage}
     * @return true if useReflect found in parameters, false otherwise
     * @throws InterceptorException
     * @throws IOException
     */
    private boolean shouldReflectEndpointBeCalled(MutableHttpServletRequest mutableRequest, OgcMessage message) throws InterceptorException, IOException {
        if (message.getService() != OgcEnum.ServiceType.WMS) {
            return false;
        }

        boolean useReflect;
        String value = MutableHttpServletRequest.getRequestParameterValue(mutableRequest, USE_REFLECT_PARAM);
        useReflect = Boolean.parseBoolean(value);
        if (useReflect) {
            log.info("Parameter " + USE_REFLECT_PARAM + "found in request. Will use WMS reflector endpoint of GeoServer.");
        }

        return useReflect;
    }

    /**
     * @param mutableRequest
     * @return
     * @throws InterceptorException
     * @throws IOException
     */
    private OgcMessage getOgcMessage(MutableHttpServletRequest mutableRequest) throws InterceptorException, IOException {
        log.trace("Building the OGC message from the given request.");
        OgcMessage ogcMessage = new OgcMessage();
        String requestService = MutableHttpServletRequest.getRequestParameterValue(
            mutableRequest, OgcEnum.Service.SERVICE.toString());
        String requestOperation = MutableHttpServletRequest.getRequestParameterValue(
            mutableRequest, OgcEnum.Operation.OPERATION.toString());
        String requestEndPoint = MutableHttpServletRequest.getRequestParameterValue(
            mutableRequest, OgcEnum.EndPoint.getAllValues());

        if (StringUtils.isEmpty(requestService) ||
            StringUtils.isEmpty(requestOperation) ||
            StringUtils.isEmpty(requestEndPoint)) {
            if (!StringUtils.isEmpty(requestEndPoint) &&
                !StringUtils.isEmpty(MutableHttpServletRequest.getRequestParameterValue(
                    mutableRequest, USE_REFLECT_PARAM))
            ) {
                log.trace("Will use WMS reflector endpoint of GeoServer");
                requestService = OgcEnum.ServiceType.WMS.toString();
                requestOperation = OgcEnum.OperationType.GET_MAP.toString();
            } else {
                throw new InterceptorException("Couldn't find all required OGC " +
                    "parameters (SERVICE, REQUEST, ENDPOINT). Please check the " +
                    "validity of the request.");
            }
        }

        if (StringUtils.isNotEmpty(requestService)) {
            ogcMessage.setService(OgcEnum.ServiceType.fromString(requestService));
            log.trace("Successfully set the service: " +
                OgcEnum.ServiceType.fromString(requestService));
        } else {
            log.debug("No service found.");
        }

        if (StringUtils.isNotEmpty(requestOperation)) {
            ogcMessage.setOperation(OgcEnum.OperationType.fromString(requestOperation));
            log.trace("Successfully set the operation: " +
                OgcEnum.OperationType.fromString(requestOperation));
        } else {
            log.debug("No operation found.");
        }

        if (StringUtils.isNotEmpty(requestEndPoint)) {
            ogcMessage.setEndPoint(requestEndPoint);
            log.trace("Successfully set the endPoint: " + requestEndPoint);
        } else {
            log.debug("No endPoint found.");
        }

        InterceptorRule mostSpecificRequestRule = getMostSpecificRule(requestService,
            requestOperation, requestEndPoint, HttpEnum.EventType.REQUEST.toString());
        InterceptorRule mostSpecificResponseRule = getMostSpecificRule(requestService,
            requestOperation, requestEndPoint, HttpEnum.EventType.RESPONSE.toString());

        if (mostSpecificRequestRule != null) {
            ogcMessage.setRequestRule(mostSpecificRequestRule.getRule());
            log.trace("Successfully set the requestRule: " +
                mostSpecificRequestRule.getRule());
        } else {
            log.debug("No interceptor rule found for the request.");
        }

        if (mostSpecificResponseRule != null) {
            ogcMessage.setResponseRule(mostSpecificResponseRule.getRule());
            log.trace("Successfully set the responseRule: " +
                mostSpecificResponseRule.getRule());
        } else {
            log.debug("No interceptor rule found for the response.");
        }

        log.trace("Successfully build the OGC message: " + ogcMessage);
        return ogcMessage;
    }

    /**
     * @param requestService
     * @param requestOperation
     * @param requestEndPoint
     * @param ruleEvent
     * @return
     * @throws InterceptorException
     */
    private InterceptorRule getMostSpecificRule(String requestService, String requestOperation, String requestEndPoint, String ruleEvent) throws InterceptorException {
        final OgcEnum.ServiceType service = OgcEnum.ServiceType.fromString(requestService);
        final OgcEnum.OperationType operation = OgcEnum.OperationType.fromString(requestOperation);
        final String endPoint = requestEndPoint;
        log.trace("""
            Finding the most specific interceptor rule for: {}
              * Event: {}
              * Service: {}
              * Operation: {}
              * EndPoint: {}
            """,
            ruleEvent, service, operation, endPoint
        );

        // get all persisted rules for the given service and event
        List<InterceptorRule> interceptorRules = this.interceptorRuleService.findAllRulesForServiceAndEvent(service, HttpEnum.EventType.fromString(ruleEvent));

        log.trace("Got " + interceptorRules.size() + " rule(s) from database.");

        if (log.isTraceEnabled()) {
            for (InterceptorRule interceptorRule : interceptorRules) {
                log.trace("Returned rule is: " + interceptorRule);
            }
        }

        log.trace("Evaluating the given rules for the most specific one:");

        // create the predicate for finding the most specific rule out of
        // the given rules (conditions in descending specific order).
        // note: we don't need to check for request event (request/response)
        // and the service type at all as the rule candidates are filtered by
        // the DAO method already. the last check for service specificity found
        // here is a fallback only.
        HashMap<InterceptorRule, Integer> ruleMap = new HashMap<>();
        interceptorRules.stream().forEach((rule) -> {
            int score = 0;

            if (!StringUtils.isEmpty(rule.getEndPoint()) && !StringUtils.isEmpty(endPoint) && !StringUtils.equalsIgnoreCase(rule.getEndPoint(), endPoint)) {
                return;
            }
            if (rule.getService() != null && !Objects.equals(rule.getService(), service) && service != null) {
                return;
            }
            if (rule.getOperation() != null && !Objects.equals(rule.getOperation(), operation) && operation != null) {
                return;
            }
            if (endPoint != null && Objects.equals(rule.getEndPoint(), endPoint)) {
                ++score;
            }
            if (operation != null && Objects.equals(rule.getOperation(), operation)) {
                ++score;
            }
            if (service != null && Objects.equals(rule.getService(), service)) {
                ++score;
            }
            ruleMap.put(rule, score);
        });

        AtomicReference<Integer> biggestScore = new AtomicReference<>(0);
        AtomicReference<InterceptorRule> mostSpecific = new AtomicReference<>();

        ruleMap.entrySet().stream().forEach((entry) -> {
            if (entry.getValue() > biggestScore.get()) {
                mostSpecific.set(entry.getKey());
                biggestScore.set(entry.getValue());
            }
        });

        if (interceptorRules.size() == 0) {
            log.error("""
                Got no interceptor rules for this request/response.
                Usually this should not happen as one has to define at
                least the basic sets of rules (e.g. ALLOW all WMS
                requests) when using the interceptor.
                """);

            throw new InterceptorException("No interceptor rule found.");
        } else if (log.isTraceEnabled()) {
            log.trace("Identified the following rule as most the specific " +
                "one: " + mostSpecific.get());
        }

        return mostSpecific.get();
    }

    /**
     * @param geoServerNamespace
     * @param useWmsReflector
     * @param isWMS
     * @throws URISyntaxException
     * @throws InterceptorException
     */
    public URI getGeoServerBaseURIFromNameSpace(String geoServerNamespace, boolean useWmsReflector, boolean isWMS) throws URISyntaxException, InterceptorException {
        String geoServerUrl = null;
        if (interceptorProperties.isNamespaceBoundUrl()) {
            Optional<NamespaceProperties> namespaceCandidate = interceptorProperties.getNamespaces().stream()
                .filter(namespace -> geoServerNamespace.equalsIgnoreCase(namespace.getNamespace()))
                .findAny();

            if (namespaceCandidate.isPresent()) {
                geoServerUrl = namespaceCandidate.get().getUrl();
            }
        } else {
            geoServerUrl = interceptorProperties.getDefaultOwsUrl();
            log.debug("Using GeoServer OWS URL without namespace: {}", geoServerUrl);
        }

        if (StringUtils.isEmpty(geoServerUrl)) {
            throw new InterceptorException("Couldn't detect GeoServer URI " +
                "from the given namespace");
        }

        if (useWmsReflector && isWMS) {
            log.trace("Will use WMS reflector endpoint");
            if (StringUtils.endsWithIgnoreCase(geoServerUrl, "ows")) {
                StringBuilder builder = new StringBuilder();
                int start = geoServerUrl.lastIndexOf("ows");
                builder.append(geoServerUrl, 0, start);
                builder.append("wms" + WMS_REFLECT_ENDPOINT);
                geoServerUrl = builder.toString();
            } else if (StringUtils.endsWithIgnoreCase(geoServerUrl, "wms")) {
                geoServerUrl += WMS_REFLECT_ENDPOINT;
            }
            log.trace("The modified endpoint is: " + geoServerUrl);
        }

        URIBuilder builder = new URIBuilder(geoServerUrl);
        return builder.build();
    }

    /**
     * @param message
     * @throws URISyntaxException
     * @throws InterceptorException
     */
    private URI getGeoServerBaseURI(OgcMessage message, boolean useWmsReflector) throws URISyntaxException, InterceptorException {
        log.debug("Finding the GeoServer base URI by the provided EndPoint: " + message.getEndPoint());

        // get the namespace from the qualified endPoint name
        String geoServerNamespace = getGeoServerNameSpace(message.getEndPoint());

        log.trace("Found the following GeoServer namespace set for endPoint: " + geoServerNamespace);

        // set the GeoServer base URL
        URI geoServerBaseUri = getGeoServerBaseURIFromNameSpace(geoServerNamespace, useWmsReflector, message.isWms());
        log.debug("The corresponding GeoServer base URI is: " + geoServerBaseUri);
        return geoServerBaseUri;
    }
}
