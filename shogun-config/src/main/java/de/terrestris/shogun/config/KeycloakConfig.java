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

import de.terrestris.shogun.properties.KeycloakProperties;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Credits to https://stackoverflow.com/questions/57787768/issues-running-example-keycloak-spring-boot-app
 */
@ConditionalOnExpression("${keycloak.enabled:true}")
@Configuration
public abstract class KeycloakConfig {

    @Autowired
    private KeycloakProperties keycloakProperties;

    @Bean
    public Keycloak keycloakAdminClient() {
        ResteasyClient restClient = new ResteasyClientBuilder()
            .hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.ANY)
            .build();

        return KeycloakBuilder.builder()
            .serverUrl(keycloakProperties.getServerUrl())
            .realm(keycloakProperties.getMasterRealm())
            .username(keycloakProperties.getUsername())
            .password(keycloakProperties.getPassword())
            .clientId(keycloakProperties.getAdminClientId())
            .resteasyClient(restClient)
            .build();
    }

    @Bean
    public RealmResource getRealm(@Autowired Keycloak kc) {
        return kc.realm(keycloakProperties.getRealm());
    }


}
