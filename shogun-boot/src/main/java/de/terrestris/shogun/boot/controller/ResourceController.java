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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@ConditionalOnExpression("${controller.resource.enabled:true}")
public class ResourceController {

    @Value("${KEYCLOAK_HOST:1.2.3.4}")
    String keycloakHost;

    @Autowired(required = false)
    BuildProperties buildProperties;

    @GetMapping("/")
    public ModelAndView home(ModelAndView modelAndView) {
        String buildVersion = "@VERSION@";
        if (buildProperties != null) {
            buildVersion = buildProperties.getVersion();
        }
        modelAndView.addObject("version", buildVersion);
        modelAndView.setViewName("index");

        return modelAndView;
    }

    @GetMapping(value = "/config/admin-client-config.js", produces = "application/javascript")
    public ModelAndView getAdminClientConfig(ModelAndView modelAndView) {
        modelAndView.addObject("KEYCLOAK_HOST", keycloakHost);
        modelAndView.setViewName("admin-client-config.js");

        return modelAndView;
    }

}
