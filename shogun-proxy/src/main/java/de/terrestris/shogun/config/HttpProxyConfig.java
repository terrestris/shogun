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
package de.terrestris.shogun.config;

import de.terrestris.shogun.properties.KeycloakProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@EnableAutoConfiguration
@ComponentScan(
    basePackages = {"de.terrestris.shogun", "${scan.package:null}"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "de.terrestris.shogun.lib.*")
)
@EnableConfigurationProperties({
    KeycloakProperties.class
})
public class HttpProxyConfig {

    public static void main(String[] args) {
        SpringApplication.run(HttpProxyConfig.class, args);
    }

}
