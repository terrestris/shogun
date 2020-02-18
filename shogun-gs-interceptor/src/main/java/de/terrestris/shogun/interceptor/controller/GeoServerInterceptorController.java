package de.terrestris.shogun.interceptor.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class GeoServerInterceptorController {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @GetMapping("/geoserver.action")
    public String info() {
        try {
            return "Hello!";
        } catch (Exception e) {
            LOG.error("Could not determine general application information: {}", e.getMessage());
            LOG.trace("Full stack trace: ", e);
        }

        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
