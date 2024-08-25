package org.example.userservice.config;

import jakarta.servlet.Filter;
import org.example.userservice.filter.AuthenticationFilter;
import org.example.userservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
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

    public SecurityConfig(UserService userService, Environment env) {
        this.userService = userService;
        this.env = env;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder());

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http
                .csrf((csrf) -> csrf.disable());
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/**").access(
                                new WebExpressionAuthorizationManager("hasIpAddress('192.168.219.102')")));
        http
                .addFilter(getAuthenticationFilter(authenticationManager));
        http
                .headers((header) -> header
                        .frameOptions((frame) -> frame.disable()));

        return http.build();
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception{
        return new AuthenticationFilter(authenticationManager);
    }

}
