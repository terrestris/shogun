package de.terrestris.shoguncore.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ComponentScan(basePackages = {
    "de.terrestris.shoguncore.controller"
})
public class WebConfig implements WebMvcConfigurer { }
