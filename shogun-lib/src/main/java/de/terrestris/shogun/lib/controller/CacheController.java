package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.service.CacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@RestController
@RequestMapping("/cache")
@ConditionalOnExpression("${controller.cache.enabled:true}")
public class CacheController {

    @Autowired
    CacheService service;

    @Autowired
    protected MessageSource messageSource;

    @PostMapping("/evict")
    public ResponseEntity<?> evictCache() {

        log.info("Requested to evict the cache.");

        try {
            service.evictCache();

            log.info("Successfully evicted the cache.");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Could not evict the cache: {}", e.getMessage());
            log.trace("Full stack trace: {}", e);

            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage(
                    "BaseController.INTERNAL_SERVER_ERROR",
                    null,
                    LocaleContextHolder.getLocale()
                ),
                e
            );
        }

    }
}
