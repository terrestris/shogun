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
package de.terrestris.shogun.boot.config;

import de.terrestris.shogun.config.WebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class BootWebSecurityConfig extends WebSecurityConfig {

    RequestMatcher csrfRequestMatcher = httpServletRequest -> {
        String refererHeader = httpServletRequest.getHeader("Referer");

        return refererHeader != null && refererHeader.endsWith("swagger-ui/index.html");
    };

    @Override
    protected void customHttpConfiguration(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers(
                    "/",
                    "/auth/**",
                    "/info/**",
                    "/index.html",
                    "/index.css",
                    "/assets/**",
                    // Enable anonymous access to swagger docs
                    "/swagger-ui/index.html",
                    "/swagger-ui/**",
                    "/webjars/springfox-swagger-ui/**",
                    "/swagger-resources/**",
                    "/v2/api-docs"
                )
                    .permitAll()
                .antMatchers(
                    "/actuator/**",
                    "/cache/**",
                    "/webhooks/**",
                    "/ws/**",
                    "/admin/**"
                )
                    .hasRole("ADMIN")
                .anyRequest()
                    .authenticated()
            .and()
                .httpBasic()
            .and()
                .formLogin()
                    .defaultSuccessUrl("/index.html")
                    .permitAll()
            .and()
                .rememberMe()
                    .key("SuPeRuNiQuErEmEmBeRmEKeY")
            .and()
                .logout()
                    .permitAll()
            .and()
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(csrfRequestMatcher)
                    .ignoringAntMatchers("/graphql")
                    .ignoringAntMatchers("/actuator/**")
                    .ignoringAntMatchers("/sso/**")
                    .ignoringAntMatchers("/ws/**");
    }

}
