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
package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.service.CacheService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@RestController
@RequestMapping("/cache")
@ConditionalOnExpression("${controller.cache.enabled:true}")
@Tag(
    name = "Cache",
    description = "The endpoints to clear the Hibernate Cache"
)
@SecurityRequirement(name = "bearer-key")
public class CacheController {

    @Autowired
    CacheService service;

    @Autowired
    protected MessageSource messageSource;

    @PostMapping("/evict")
    public ResponseEntity<?> evictCache(
        @RequestParam(required = false) List<String> regions,
        @RequestParam(required = false) List<String> queryRegions
    ) {

        log.info("Requested to evict the cache.");

        try {
            if ((regions == null || regions.isEmpty()) && (queryRegions == null || queryRegions.isEmpty())) {
                service.evictCache();
                log.info("Successfully evicted all cache regions.");
            } else {
                service.evictCacheRegions(regions, queryRegions);
                log.info("Successfully evicted cache regions {}", regions);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Could not evict the cache: {}", e.getMessage());
            log.trace("Full stack trace: ", e);

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
