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
package de.terrestris.shogun.interceptor.controller;

import de.terrestris.shogun.interceptor.exception.InterceptorException;
import de.terrestris.shogun.interceptor.service.GeoServerInterceptorService;
import de.terrestris.shogun.lib.dto.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.core5.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@Log4j2
public class GeoServerInterceptorController {
    public static final String ERROR_MESSAGE = "Error while requesting a GeoServer resource: ";

    @Autowired
    protected GeoServerInterceptorService service;

    @RequestMapping(value = {"/geoserver.action", "/geoserver.action/{endpoint}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<byte[]> interceptGeoServerRequest(HttpServletRequest request, Optional<String> endpoint) {
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus responseStatus;
        byte[] responseBody;
        HttpResponse httpResponse;

        try {
            log.trace("Trying to intercept a GeoServer resource.");
            httpResponse = this.service.interceptGeoServerRequest(request, endpoint);
            responseStatus = httpResponse.getStatusCode();
            responseBody = httpResponse.getBody();
            responseHeaders = httpResponse.getHeaders();

            log.trace("Successfully intercepted a GeoServer resource.");
            return new ResponseEntity<>(responseBody, responseHeaders, responseStatus);
        } catch (NullPointerException | IOException | InterceptorException | HttpException | URISyntaxException e) {
            log.error(ERROR_MESSAGE + "{}", e.getMessage());
            log.trace("Full stack trace: ", e);
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + e.getMessage(), e);
        }
    }
}
