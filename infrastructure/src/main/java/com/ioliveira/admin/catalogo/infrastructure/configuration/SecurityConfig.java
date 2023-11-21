package com.ioliveira.admin.catalogo.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private static final String ROLE_ADMIN = "catalogo_admin";
    private static final String ROLE_CAST_MEMBERS = "catalogo_cast_members";
    private static final String ROLE_CATEGORIES = "catalogo_categories";
    private static final String ROLE_GENRES = "catalogo_genres";

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        // csrf -> Desabilita a proteção de ataques CSRF.
        // https://docs.spring.io/spring-security/reference/features/exploits/csrf.html#csrf-when
        // When should you use CSRF protection? Our recommendation is to use CSRF protection for any request that
        // could be processed by a browser by normal users. If you are creating a service that is used only
        // by non-browser clients, you likely want to disable CSRF protection.
        httpSecurity
                .csrf()
                .disable();

        httpSecurity.authorizeHttpRequests()
                .antMatchers("/cast_members*").hasAnyRole(ROLE_ADMIN, ROLE_CAST_MEMBERS)
                .antMatchers("/categories*").hasAnyRole(ROLE_ADMIN, ROLE_CATEGORIES)
                .antMatchers("/genres*").hasAnyRole(ROLE_ADMIN, ROLE_GENRES)
                .anyRequest().hasAnyRole(ROLE_ADMIN);// Caso não seja nenhuma das rotas acima, precisa ter role de admin

        httpSecurity
                .oauth2ResourceServer()
                .jwt();

        httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity
                .headers()
                .frameOptions()
                .sameOrigin();

        return httpSecurity.build();
    }

}
