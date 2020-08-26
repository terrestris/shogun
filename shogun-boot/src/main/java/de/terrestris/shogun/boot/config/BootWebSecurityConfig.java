package de.terrestris.shogun.boot.config;

import de.terrestris.shogun.config.WebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class BootWebSecurityConfig extends WebSecurityConfig {

    RequestMatcher csrfRequestMatcher = new RequestMatcher() {
        public boolean matches(HttpServletRequest httpServletRequest) {
            String refererHeader = httpServletRequest.getHeader("Referer");

            if (refererHeader != null && refererHeader.endsWith("swagger-ui.html")) {
                return true;
            }

            return false;
        }
    };

    @Override
    protected void customHttpConfiguration(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers(
                    "/",
                    "/auth/**",
                    "/index.html"
                )
                    .permitAll()
                .antMatchers(
                    "/swagger-ui.html",
                    "/actuator/**"
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
