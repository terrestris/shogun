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

import de.terrestris.shogun.interceptor.config.properties.InterceptorProperties;
import de.terrestris.shogun.interceptor.config.properties.NamespaceProperties;
import de.terrestris.shogun.lib.envers.ShogunEnversRevisionRepositoryFactoryBean;
import de.terrestris.shogun.properties.KeycloakAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
    basePackages = {"de.terrestris.shogun", "${scan.package:null}"},
    repositoryFactoryBeanClass = ShogunEnversRevisionRepositoryFactoryBean.class
)
@ComponentScan(basePackages = {"de.terrestris.shogun", "${scan.package:null}"})
@EntityScan(basePackages = {"de.terrestris.shogun", "${scan.package:null}"})
@EnableConfigurationProperties({
    InterceptorProperties.class,
    NamespaceProperties.class,
    KeycloakAuthProperties.class
})
public class InterceptorApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(InterceptorApplicationConfig.class, args);
    }

}
