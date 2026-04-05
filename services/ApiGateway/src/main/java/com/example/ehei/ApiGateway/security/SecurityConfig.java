package com.example.ehei.ApiGateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        log.info(">>> ReactiveJwtDecoder bean initialisé");
        return NimbusReactiveJwtDecoder
                .withJwkSetUri("http://keycloak:8080/realms/hospital-realm/protocol/openid-connect/certs")
                .build();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder) {
        log.info(">>> SecurityWebFilterChain initialisé");
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // routes publiques — pas de JWT requis
                        .pathMatchers("/auth/login", "/auth/refresh").permitAll()
                        // toutes les autres routes nécessitent un JWT valide
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        // utilise notre decoder custom — vérifie la signature sans valider l'issuer
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder))
                );

        return http.build();
    }
}