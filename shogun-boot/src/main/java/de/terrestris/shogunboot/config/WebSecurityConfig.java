package de.terrestris.shogunboot.config;

import de.terrestris.shoguncore.security.ShogunUserDetailsService;
import de.terrestris.shoguncore.security.authentication.ShogunAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ShogunAuthenticationProvider authProvider;

    @Autowired
    public ShogunUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
//            .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
            .and()
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringAntMatchers("/graphql");
    }

//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user =
//            User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
}
