package de.terrestris.shogun.boot.config;

import de.terrestris.shogun.config.WebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class BootWebSecurityConfig extends WebSecurityConfig {

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
                    .ignoringAntMatchers("/graphql");
    }

}
