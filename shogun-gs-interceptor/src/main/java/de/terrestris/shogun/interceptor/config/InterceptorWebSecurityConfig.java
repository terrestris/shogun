package de.terrestris.shogun.interceptor.config;

import de.terrestris.shogun.config.WebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

@Configuration
public class InterceptorWebSecurityConfig extends WebSecurityConfig {

    @Override
    protected ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry customHttpConfiguration(HttpSecurity http) throws Exception {
        return http.authorizeRequests()
            .antMatchers(
                // Allow access to swagger interface
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/v2/**",
                "/csrf/**"
            )
            .permitAll()
            .anyRequest()
            .hasRole("user");
    }
}
