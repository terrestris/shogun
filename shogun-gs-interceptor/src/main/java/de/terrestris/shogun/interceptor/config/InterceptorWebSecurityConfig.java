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

import de.terrestris.shogun.config.DefaultWebSecurityConfig;
import org.apache.commons.lang3.Strings;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class InterceptorWebSecurityConfig implements DefaultWebSecurityConfig {

    RequestMatcher csrfRequestMatcher = httpServletRequest -> {
        String refererHeader = httpServletRequest.getHeader("Referer");
        return Strings.CI.equals(refererHeader, "Shogun-Manager-Client");
    };

    @Override
    public void customHttpConfiguration(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    // Allow access to swagger interface
                    "/swagger-ui/index.html",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/v3/**",
                    "/csrf/**"
                )
                    .permitAll()
                .requestMatchers("/interceptorrules/**")
                    .hasRole("INTERCEPTOR_ADMIN")
                .anyRequest()
                    .authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(csrfRequestMatcher)
            );
    }

}
