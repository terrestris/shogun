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

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * https://stackoverflow.com/questions/40769200/configure-spring-boot-for-spa-frontend
 */
@Controller
@ConditionalOnExpression("${controller.singlepageapp.enabled:true}")
public class SinglePageAppController {

    @GetMapping("/**/{path:[^\\.]*}")
    public String redirect(HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/admin")) {
            return "forward:/admin/index.html";
        }

        if (request.getRequestURI().startsWith("/client")) {
            return "forward:/client/index.html";
        }

        return "forward:/";
    }

}
