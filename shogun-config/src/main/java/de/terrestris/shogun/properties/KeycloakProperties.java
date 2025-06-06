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
package de.terrestris.shogun.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Component
public class KeycloakProperties {

    private Boolean enabled;

    private String serverUrl;

    private String masterRealm;

    private String adminClientId;

    private String adminClientSecret;

    private String realm;

    private String clientId;

    private String principalAttribute;

    private Boolean disableHostnameVerification = false;

    private Boolean extractRolesFromResource = true;

    private Boolean extractRolesFromRealm = false;

}
