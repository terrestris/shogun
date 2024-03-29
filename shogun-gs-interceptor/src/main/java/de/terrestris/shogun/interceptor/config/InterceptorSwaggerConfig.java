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
package de.terrestris.shogun.interceptor.config;

import de.terrestris.shogun.lib.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class InterceptorSwaggerConfig extends SwaggerConfig {

    @Override
    protected OpenAPI apiInfo() {
        OpenAPI api = super.apiInfo();

        api.getInfo().setTitle("SHOGun GeoServer Interceptor REST-API");

        return api;
    }

}
