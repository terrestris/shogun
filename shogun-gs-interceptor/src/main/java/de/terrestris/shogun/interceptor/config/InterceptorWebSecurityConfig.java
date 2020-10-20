package de.terrestris.shogun.interceptor.config;

import de.terrestris.shogun.config.WebSecurityConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class InterceptorWebSecurityConfig extends WebSecurityConfig {

    RequestMatcher csrfRequestMatcher = httpServletRequest -> {
        String refererHeader = httpServletRequest.getHeader("Referer");
        return StringUtils.equalsIgnoreCase(refererHeader, "Shogun-Manager-Client");
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
            .antMatchers("/interceptorrules/**")
                .hasRole("INTERCEPTOR_ADMIN")
            .anyRequest()
                .authenticated()
            .and()
                .httpBasic()
            .and()
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(csrfRequestMatcher);
    }
}
