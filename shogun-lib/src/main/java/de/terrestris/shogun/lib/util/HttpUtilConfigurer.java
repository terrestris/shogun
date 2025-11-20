/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2025-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class HttpUtilConfigurer {

    /**
     * The HTTP timeout in milliseconds. Default is 15000 ms (15 seconds).
     */
    @Value("${http.timeout:15000}")
    private int httpTimeout;

    @PostConstruct
    public void configureHttpUtil() {
        if (httpTimeout < 0) {
            throw new IllegalArgumentException("Timeout value cannot be negative");
        }

        if (httpTimeout == 0) {
            log.warn("Configuring HttpUtil with timeout set to 0 ms. This means no timeout!");
        } else {
            log.info("Configuring HttpUtil with timeout: {}", httpTimeout);
        }
        HttpUtil.defaultHttpTimeout = httpTimeout;
        HttpUtil.httpTimeout = httpTimeout;
    }

}
