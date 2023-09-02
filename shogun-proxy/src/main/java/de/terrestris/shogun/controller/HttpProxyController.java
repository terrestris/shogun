/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2021-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.controller;

import de.terrestris.shogun.service.HttpProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Controller for simple HTTP Proxy service (forward proxy)
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
@Controller
public class HttpProxyController {

    @Autowired
    protected HttpProxyService httpProxyService;

    /**
     * Web controller mapping <i>proxy.action</i> to doProxy method. Provided parameters are passed to {@link HttpProxyService}
     *
     * @param request {@link HttpServletRequest} to use in proxy (e.g. to obtain headers from)
     * @param baseUrl The base url of request
     * @param params  Request params
     * @return ResponseEntity
     */
    @RequestMapping("/proxy.action")
    public @ResponseBody
    ResponseEntity<?> doProxy(HttpServletRequest request, @RequestParam String baseUrl, @RequestParam(required = false) Map<String, String> params) {
        return httpProxyService.doProxy(request, baseUrl, params);
    }

}
