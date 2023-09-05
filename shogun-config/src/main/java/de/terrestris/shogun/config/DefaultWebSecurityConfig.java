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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

public interface DefaultWebSecurityConfig extends WebSecurityConfig {

    default void customHttpConfiguration(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests()
                .requestMatchers(
                    "/",
                    "/auth/**",
                    "/info/**",
                    "/index.html",
                    "/index.css",
                    "/favicon.ico",
                    "/assets/**",
                    // Enable anonymous access to swagger docs
                    "/swagger-ui/index.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/swagger-config",
                    // Enable anonymous access to GraphiQL
                    "/graphiql/**"
                )
                    .permitAll()
                .requestMatchers(
                    "/actuator/**",
                    "/cache/**",
                    "/webhooks/**",
                    "/ws/**"
                )
                    .hasRole("ADMIN")
                .anyRequest()
                    .authenticated()
            .and()
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                    .ignoringRequestMatchers(csrfRequestMatcher)
                    .ignoringRequestMatchers("/graphql")
                    .ignoringRequestMatchers("/actuator/**")
                    .ignoringRequestMatchers("/sso/**")
                    .ignoringRequestMatchers("/ws/**");
    }

    default void configure(HttpSecurity http) throws Exception {
        customHttpConfiguration(http);
    }

}
