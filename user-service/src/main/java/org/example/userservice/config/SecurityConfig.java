package org.example.userservice.config;

import jakarta.servlet.Filter;
import org.example.userservice.filter.AuthenticationFilter;
import org.example.userservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final Environment env;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectPostProcessor objectPostProcessor;

    public SecurityConfig(UserService userService, Environment env, BCryptPasswordEncoder passwordEncoder, ObjectPostProcessor objectPostProcessor) {
        this.userService = userService;
        this.env = env;
        this.passwordEncoder = passwordEncoder;
        this.objectPostProcessor = objectPostProcessor;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable());
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/**").access(
                                new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1') or hasIpAddress('192.168.219.102')")));
        http
                .addFilter(getAuthenticationFilter());
        http
                .headers((header) -> header
                        .frameOptions((frame) -> frame.disable()));

        return http.build();
    }

    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception{
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        AuthenticationManagerBuilder authenticationManagerBuilder =
                new AuthenticationManagerBuilder(objectPostProcessor);
        authenticationFilter.setAuthenticationManager(authenticationManager(authenticationManagerBuilder));
        return authenticationFilter;
    }

}
