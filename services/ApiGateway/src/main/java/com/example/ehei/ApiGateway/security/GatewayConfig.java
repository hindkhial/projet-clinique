package com.example.ehei.ApiGateway.security;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Optional;

@Configuration
public class GatewayConfig {

    /**
     * Filtre global exécuté sur chaque requête passant par la gateway.
     * Si la requête est authentifiée (JWT valide), on extrait les informations
     * de l'utilisateur et on les transmet aux microservices sous forme de headers.
     * Ainsi, les microservices n'ont pas besoin de valider le JWT eux-mêmes —
     * ils lisent simplement X-User-Id, X-User-Email, X-User-Roles depuis les headers.
     */
    @Bean
    public GlobalFilter jwtHeadersFilter() {
        return (exchange, chain) -> exchange.getPrincipal()
                // on ne traite que si le principal est un token JWT
                .filter(principal -> principal instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    Jwt jwt = auth.getToken();

                    // claim "sub" — identifiant unique de l'utilisateur assigné par Keycloak
                    String userId = jwt.getSubject();

                    // claim "email" — peut être null si non configuré dans Keycloak
                    String email = jwt.getClaimAsString("email");

                    // claim "realm_access.roles" — liste des rôles assignés au niveau du realm
                    // ex: ["DOCTOR", "ADMIN", "offline_access"]
                    List<String> roles = Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
                            .map(map -> (List<String>) map.get("roles"))
                            .orElse(List.of()); // liste vide si le claim est absent

                    // on modifie la requête entrante en ajoutant les infos utilisateur en headers
                    // les services en aval peuvent lire ces headers directement
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Email", email != null ? email : "")
                            .header("X-User-Roles", String.join(",", roles)) // ex: "DOCTOR,ADMIN"
                            .build();

                    // on transmet la requête modifiée à la suite de la chaîne de filtres
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                // si pas de JWT (routes publiques comme /auth/login),
                // on laisse passer la requête telle quelle sans ajouter de headers
                .switchIfEmpty(chain.filter(exchange));
    }
}