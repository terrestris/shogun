package de.terrestris.shogunboot.controller;

import de.terrestris.shogunboot.dto.ApplicationInfo;
import de.terrestris.shogunboot.service.ApplicationInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller that delivers general application information.
 */
@RestController
@RequestMapping("/info")
@ConditionalOnExpression("${controller.info.enabled:true}")
public class ApplicationInfoController {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    private ApplicationInfoService infoService;

    /**
     * Application info endpoint.
     *
     * @return the general application info
     */
    @GetMapping("/app")
    public ApplicationInfo info() {
        try {
            return infoService.getApplicationInfo();
        } catch (Exception e) {
            LOG.error("Could not determine general application information: {}", e.getMessage());
            LOG.trace("Full stack trace: ", e);
        }

        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
