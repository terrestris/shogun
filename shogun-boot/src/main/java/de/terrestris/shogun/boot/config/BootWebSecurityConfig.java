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

        return refererHeader != null && refererHeader.endsWith("swagger-ui.html");
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
                    // Enable anonymous access to swagger docs
                    "/swagger-ui.html",
                    "/webjars/springfox-swagger-ui/**",
                    "/swagger-resources/**",
                    "/v2/api-docs"
                )
                    .permitAll()
                .antMatchers(
                    "/actuator/**",
                    "/cache/**"
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
                    .ignoringAntMatchers("/actuator/**");
    }

}
