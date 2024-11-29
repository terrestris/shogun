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

import de.terrestris.shogun.converter.KeycloakJwtAuthenticationConverter;
import de.terrestris.shogun.properties.KeycloakProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import javax.net.ssl.HttpsURLConnection;

@ConditionalOnExpression("${keycloak.enabled:true}")
@Configuration
@EnableWebSecurity
public class KeycloakWebSecurityConfig implements DefaultWebSecurityConfig {

    @Autowired
    private ApplicationContext applicationContext;

    // https://stackoverflow.com/questions/74710493/spring-boot-3-0-security-6-0-migration-el1057e-no-bean-resolver-registered-i
    private WebExpressionAuthorizationManager getWebExpressionAuthorizationManager(final String expression) {
        final var expressionHandler = new DefaultHttpSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        final var authorizationManager = new WebExpressionAuthorizationManager(expression);
        authorizationManager.setExpressionHandler(expressionHandler);
        return authorizationManager;
    }

    @Autowired
    private KeycloakProperties keycloakProperties;

    @Value("${KEYCLOAK_HOST:shogun-keycloak}")
    private String keycloakHost;

    @PostConstruct
    public void init() {
        if (keycloakProperties.getDisableHostnameVerification()) {
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) ->
                (hostname.equals("localhost") || hostname.equals(keycloakHost)));
        }
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((auths) -> auths
                .requestMatchers("/webhooks/keycloak/**")
                    .access(getWebExpressionAuthorizationManager(
                        "hasRole('ROLE_ADMIN') or @keycloakUtil.isInternalKeycloakRequest(request)")
                    )
            );

        customHttpConfiguration(http);

        KeycloakJwtAuthenticationConverter authConverter = new KeycloakJwtAuthenticationConverter(
            keycloakProperties.getClientId(),
            keycloakProperties.getPrincipalAttribute(),
            keycloakProperties.getExtractRolesFromRealm(),
            keycloakProperties.getExtractRolesFromResource()
        );

        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/webhooks/**")
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(authConverter))
            );
    }

}
