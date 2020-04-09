package de.terrestris.shogun.importer.config;

import de.terrestris.shogun.config.WebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class ImporterWebSecurityConfig extends WebSecurityConfig {

    RequestMatcher csrfRequestMatcher = new RequestMatcher() {
        public boolean matches(HttpServletRequest httpServletRequest) {
            if (httpServletRequest.getHeader("Referer").endsWith("swagger-ui.html")) {
                return false;
            }
            return true;
        }
    };

    @Override
    protected void customHttpConfiguration(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers(
                // Allow access to swagger interface
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/v2/**",
                "/csrf/**"
            )
                .permitAll()
            .antMatchers(
                "/graphiql/**",
                "/import/**"
            )
                .authenticated()
            .and()
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(csrfRequestMatcher);
    }
}
