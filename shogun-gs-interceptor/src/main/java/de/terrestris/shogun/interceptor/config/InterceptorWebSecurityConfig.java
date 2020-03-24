package de.terrestris.shogun.interceptor.config;

import de.terrestris.shogun.config.WebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class InterceptorWebSecurityConfig extends WebSecurityConfig {

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
                .hasRole("interceptor-admin")
            .anyRequest()
                .authenticated();
    }
}
