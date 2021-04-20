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
package de.terrestris.shogun.boot.controller;

import de.terrestris.shogun.boot.dto.ApplicationInfo;
import de.terrestris.shogun.boot.service.ApplicationInfoService;
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
