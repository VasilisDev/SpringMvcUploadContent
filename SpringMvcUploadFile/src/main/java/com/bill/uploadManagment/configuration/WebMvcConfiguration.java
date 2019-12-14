package com.bill.uploadManagment.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/login/form")
                .setViewName("login");
        registry.addViewController("/errors/403")
                .setViewName("/errors/403");
        registry.addViewController("/session-invalid")
                 .setViewName("/session-invalid.html");
        registry.addViewController("/session-expired")
                 .setViewName("/session-expired.html");

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
