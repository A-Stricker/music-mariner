package com.amandastricker.musicmariner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.context.annotation.PropertySource;


@Configuration
@EnableWebSecurity
@PropertySource("classpath:application-secret.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/", "/error", "/webjars/**", "/oauth/**").permitAll()  // Allow access to these without authentication
                                .anyRequest().authenticated()  // Require authentication for any other request
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .defaultSuccessUrl("/playlist", true) // Redirect to /playlist after success
                )
                .logout(logout ->
                                logout
                                        .logoutSuccessUrl("/").permitAll()  // After logout, redirect to home
                )

                ;
    }
}



