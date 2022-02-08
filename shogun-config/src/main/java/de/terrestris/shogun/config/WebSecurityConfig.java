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
package de.terrestris.shogun.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.RequestMatcher;

public interface WebSecurityConfig {

    RequestMatcher csrfRequestMatcher = httpServletRequest -> {
        String refererHeader = httpServletRequest.getHeader("Referer");

        return refererHeader != null && refererHeader.endsWith("swagger-ui/index.html");
    };

    default void configure(HttpSecurity http) throws Exception {
        customHttpConfiguration(http);
    }

    void customHttpConfiguration(HttpSecurity http) throws Exception;

}
