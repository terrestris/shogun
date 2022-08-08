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
package de.terrestris.shogun.interceptor.message;

import de.terrestris.shogun.interceptor.exception.InterceptorException;
import de.terrestris.shogun.interceptor.request.WcsRequestInterceptorInterface;
import de.terrestris.shogun.interceptor.request.WfsRequestInterceptorInterface;
import de.terrestris.shogun.interceptor.request.WmsRequestInterceptorInterface;
import de.terrestris.shogun.interceptor.request.WpsRequestInterceptorInterface;
import de.terrestris.shogun.interceptor.response.WcsResponseInterceptorInterface;
import de.terrestris.shogun.interceptor.response.WfsResponseInterceptorInterface;
import de.terrestris.shogun.interceptor.response.WmsResponseInterceptorInterface;
import de.terrestris.shogun.interceptor.response.WpsResponseInterceptorInterface;
import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import de.terrestris.shogun.lib.dto.HttpResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
@Log4j2
public class OgcMessageDistributor {

    private static final String MODIFYING_REQUEST_MSG = "Modifying a {0} {1} request";
    private static final String MODIFYING_RESPONSE_MSG = "Modifying a {0} {1} response";
    private static final String REQUEST_IMPLEMENTATION_NOT_FOUND_MSG =
        "No interceptor class implementation for request {0} {1} found. " +
            "Forwarding the original request.";
    private static final String RESPONSE_IMPLEMENTATION_NOT_FOUND_MSG =
        "No interceptor class implementation for response {0} {1} found. " +
            "Returning the original response.";
    private static final String REQUEST_NOT_SUPPORTED_MSG = "The request type " +
        "{0} is not supported";
    private static final String RESPONSE_NOT_SUPPORTED_MSG = "The response type " +
        "{0} is not supported";
    @Autowired(required = false)
    @Qualifier("wmsRequestInterceptor")
    private WmsRequestInterceptorInterface wmsRequestInterceptor;

    @Autowired(required = false)
    @Qualifier("wfsRequestInterceptor")
    private WfsRequestInterceptorInterface wfsRequestInterceptor;

    @Autowired(required = false)
    @Qualifier("wcsRequestInterceptor")
    private WcsRequestInterceptorInterface wcsRequestInterceptor;

    @Autowired(required = false)
    @Qualifier("wpsRequestInterceptor")
    private WpsRequestInterceptorInterface wpsRequestInterceptor;

    @Autowired(required = false)
    @Qualifier("wmsResponseInterceptor")
    private WmsResponseInterceptorInterface wmsResponseInterceptor;

    @Autowired(required = false)
    @Qualifier("wfsResponseInterceptor")
    private WfsResponseInterceptorInterface wfsResponseInterceptor;

    @Autowired(required = false)
    @Qualifier("wcsResponseInterceptor")
    private WcsResponseInterceptorInterface wcsResponseInterceptor;

    @Autowired(required = false)
    @Qualifier("wpsResponseInterceptor")
    private WpsResponseInterceptorInterface wpsResponseInterceptor;

    public OgcMessageDistributor() {
    }

    /**
     * @param mutableRequest
     * @param response
     * @param message
     * @return
     * @throws InterceptorException
     */
    public HttpResponse distributeToResponseInterceptor(MutableHttpServletRequest mutableRequest, HttpResponse response, OgcMessage message) throws InterceptorException {
        if (message.isResponseAllowed()) {
            log.debug("Response is ALLOWED, not intercepting the response.");
            return response;
        } else if (message.isResponseDenied()) {
            throw new InterceptorException("Response is DENIED, blocking the response.");
        } else if (message.isResponseModified()) {
            log.debug("Response is to be MODIFIED, intercepting the response.");
        }

        String implErrMsg = MessageFormat.format(RESPONSE_IMPLEMENTATION_NOT_FOUND_MSG,
            message.getService(), message.getOperation());
        String infoMsg = MessageFormat.format(MODIFYING_RESPONSE_MSG,
            message.getService(), message.getOperation());
        String serviceErrMsg = MessageFormat.format(RESPONSE_NOT_SUPPORTED_MSG,
            message.getService());
        String operationErrMsg = MessageFormat.format(RESPONSE_NOT_SUPPORTED_MSG,
            message.getOperation());

        if (message.isWms()) {

            // check if the wmsResponseInterceptor is available
            if (this.wmsResponseInterceptor == null) {
                log.debug(implErrMsg);
                return response;
            }

            log.debug(infoMsg);

            if (message.isWmsGetCapabilities()) {
                response = this.wmsResponseInterceptor.interceptGetCapabilities(mutableRequest, response);
            } else if (message.isWmsGetMap()) {
                response = this.wmsResponseInterceptor.interceptGetMap(mutableRequest, response);
            } else if (message.isWmsGetFeatureInfo()) {
                response = this.wmsResponseInterceptor.interceptGetFeatureInfo(mutableRequest, response);
            } else if (message.isWmsGetLegendGraphic()) {
                response = this.wmsResponseInterceptor.interceptGetLegendGraphic(mutableRequest, response);
            } else if (message.isWmsGetStyles()) {
                response = this.wmsResponseInterceptor.interceptGetStyles(mutableRequest, response);
            } else if (message.isWmsDescribeLayer()) {
                response = this.wmsResponseInterceptor.interceptDescribeLayer(mutableRequest, response);
            } else {
                throw new InterceptorException(operationErrMsg);
            }

        } else if (message.isWfs()) {

            // check if the wfsResponseInterceptor is available
            if (this.wfsResponseInterceptor == null) {
                log.debug(implErrMsg);
                return response;
            }

            log.debug(infoMsg);

            // Note: WFS 2.0.0 operations are not supported yet!
            if (message.isWfsGetCapabilities()) {
                response = this.wfsResponseInterceptor.interceptGetCapabilities(mutableRequest, response);
            } else if (message.isWfsGetFeature()) {
                response = this.wfsResponseInterceptor.interceptGetFeature(mutableRequest, response);
            } else if (message.isWfsDescribeFeatureType()) {
                response = this.wfsResponseInterceptor.interceptDescribeFeatureType(mutableRequest, response);
            } else if (message.isWfsTransaction()) {
                response = this.wfsResponseInterceptor.interceptTransaction(mutableRequest, response);
            } else if (message.isWfsLockFeature()) {
                response = this.wfsResponseInterceptor.interceptLockFeature(mutableRequest, response);
            } else {
                throw new InterceptorException(operationErrMsg);
            }

        } else if (message.isWcs()) {

            // check if the wcsResponseInterceptor is available
            if (this.wcsResponseInterceptor == null) {
                log.debug(implErrMsg);
                return response;
            }

            log.debug(infoMsg);

            if (message.isWcsGetCapabilities()) {
                response = this.wcsResponseInterceptor.interceptGetCapabilities(mutableRequest, response);
            } else if (message.isWcsDescribeCoverage()) {
                response = this.wcsResponseInterceptor.interceptDescribeCoverage(mutableRequest, response);
            } else if (message.isWcsGetCoverage()) {
                response = this.wcsResponseInterceptor.interceptGetCoverage(mutableRequest, response);
            } else {
                throw new InterceptorException(operationErrMsg);
            }

        } else if (message.isWps()) {

            // check if the wpsResponseInterceptor is available
            if (this.wpsResponseInterceptor == null) {
                log.debug(implErrMsg);
                return response;
            }

            log.debug(infoMsg);

            if (message.isWpsGetCapabilities()) {
                response = this.wpsResponseInterceptor.interceptGetCapabilities(mutableRequest, response);
            } else if (message.isWpsDescribeProcess()) {
                response = this.wpsResponseInterceptor.interceptDescribeProcess(mutableRequest, response);
            } else if (message.isWpsExecute()) {
                response = this.wpsResponseInterceptor.interceptExecute(mutableRequest, response);
            } else {
                throw new InterceptorException(operationErrMsg);
            }

        } else {
            throw new InterceptorException(serviceErrMsg);
        }

        if (response == null) {
            throw new InterceptorException("The response object is null. " +
                "Please check your ResponseInterceptor implementation.");
        }

        return response;
    }

    /**
     * @param request
     * @param message
     * @return
     * @throws InterceptorException
     */
    public MutableHttpServletRequest distributeToRequestInterceptor(MutableHttpServletRequest request, OgcMessage message) throws InterceptorException {

        if (message.isRequestAllowed()) {
            log.debug("Request is ALLOWED, not intercepting the request.");
            return request;
        } else if (message.isRequestDenied()) {
            throw new InterceptorException("Request is DENIED, blocking the request.");
        } else if (message.isRequestModified()) {
            log.debug("Request is to be MODIFIED, intercepting the request.");
        }

        String implErrMsg = MessageFormat.format(REQUEST_IMPLEMENTATION_NOT_FOUND_MSG,
            message.getService(), message.getOperation());
        String infoMsg = MessageFormat.format(MODIFYING_REQUEST_MSG,
            message.getService(), message.getOperation());
        String serviceErrMsg = MessageFormat.format(REQUEST_NOT_SUPPORTED_MSG,
            message.getService());
        String operationErrMsg = MessageFormat.format(REQUEST_NOT_SUPPORTED_MSG,
            message.getOperation());

        if (message.isWms()) {

            // check if the wmsRequestInterceptor is available
            if (this.wmsRequestInterceptor == null) {
                log.debug(implErrMsg);
                return request;
            }

            log.debug(infoMsg);

            if (message.isWmsGetCapabilities()) {
                request = this.wmsRequestInterceptor.interceptGetCapabilities(request);
            } else if (message.isWmsGetMap()) {
                request = this.wmsRequestInterceptor.interceptGetMap(request);
            } else if (message.isWmsGetFeatureInfo()) {
                request = this.wmsRequestInterceptor.interceptGetFeatureInfo(request);
            } else if (message.isWmsGetLegendGraphic()) {
                request = this.wmsRequestInterceptor.interceptGetLegendGraphic(request);
            } else if (message.isWmsGetStyles()) {
                request = this.wmsRequestInterceptor.interceptGetStyles(request);
            } else if (message.isWmsDescribeLayer()) {
                request = this.wmsRequestInterceptor.interceptDescribeLayer(request);
            } else {
                throw new InterceptorException(operationErrMsg);
            }

        } else if (message.isWfs()) {

            // check if the wfsRequestInterceptor is available
            if (this.wfsRequestInterceptor == null) {
                log.debug(implErrMsg);
                return request;
            }

            log.debug(infoMsg);

            // Note: WFS 2.0.0 operations are not supported yet!
            if (message.isWfsGetCapabilities()) {
                request = this.wfsRequestInterceptor.interceptGetCapabilities(request);
            } else if (message.isWfsGetFeature()) {
                request = this.wfsRequestInterceptor.interceptGetFeature(request);
            } else if (message.isWfsDescribeFeatureType()) {
                request = this.wfsRequestInterceptor.interceptDescribeFeatureType(request);
            } else if (message.isWfsTransaction()) {
                request = this.wfsRequestInterceptor.interceptTransaction(request);
            } else if (message.isWfsLockFeature()) {
                request = this.wfsRequestInterceptor.interceptLockFeature(request);
            } else {
                throw new InterceptorException(operationErrMsg);
            }

        } else if (message.isWcs()) {

            // check if the wcsRequestInterceptor is available
            if (this.wcsRequestInterceptor == null) {
                log.debug(implErrMsg);
                return request;
            }

            log.debug(infoMsg);

            if (message.isWcsGetCapabilities()) {
                request = this.wcsRequestInterceptor.interceptGetCapabilities(request);
            } else if (message.isWcsDescribeCoverage()) {
                request = this.wcsRequestInterceptor.interceptDescribeCoverage(request);
            } else if (message.isWcsGetCoverage()) {
                request = this.wcsRequestInterceptor.interceptGetCoverage(request);
            } else {
                throw new InterceptorException(operationErrMsg);
            }

        } else if (message.isWps()) {

            // check if the wpsRequestInterceptor is available
            if (this.wpsRequestInterceptor == null) {
                log.debug(implErrMsg);
                return request;
            }

            if (message.isWpsGetCapabilities()) {
                request = this.wpsRequestInterceptor.interceptGetCapabilities(request);
            } else if (message.isWpsDescribeProcess()) {
                request = this.wpsRequestInterceptor.interceptDescribeProcess(request);
            } else if (message.isWpsExecute()) {
                request = this.wpsRequestInterceptor.interceptExecute(request);
            } else {
                throw new InterceptorException(operationErrMsg);
            }

            return request;

        } else {
            throw new InterceptorException(serviceErrMsg);
        }

        if (request == null) {
            throw new InterceptorException("The request object is null. " +
                "Please check your RequestInterceptor implementation.");
        }

        return request;

    }
}
