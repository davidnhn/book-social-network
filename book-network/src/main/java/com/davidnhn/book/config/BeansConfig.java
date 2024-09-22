package com.davidnhn.book.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpHeaders.*;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    private  final UserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuditorAware<Integer> auditorProvider() {
        return new ApplicationAuditAware();
    }

    // Déclare une méthode comme un bean Spring qui sera géré par le conteneur Spring.
    @Bean
    public CorsFilter corsFilter() {
        // Crée une nouvelle instance de UrlBasedCorsConfigurationSource, qui est utilisée pour gérer la configuration CORS basée sur les URL.
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Crée une nouvelle instance de CorsConfiguration, qui est utilisée pour définir la configuration CORS.
        final CorsConfiguration config = new CorsConfiguration();

        // Permet l'envoi de cookies et autres informations d'identification dans les requêtes CORS.
        config.setAllowCredentials(true);

        // Spécifie les origines autorisées à accéder aux ressources. Ici, seul "http://localhost:4200" est autorisé.
//        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:8080"));

        // Spécifie les en-têtes HTTP autorisés dans les requêtes CORS.
        config.setAllowedHeaders(Arrays.asList(
                ORIGIN,          // En-tête spécifiant l'origine de la requête.
                CONTENT_TYPE,    // En-tête spécifiant le type de contenu de la requête.
                ACCEPT,          // En-tête indiquant les types de contenu que le client est prêt à accepter.
                AUTHORIZATION    // En-tête utilisé pour transmettre des informations d'autorisation.
        ));

        // Spécifie les méthodes HTTP autorisées pour les requêtes CORS.
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTION"));

        // Enregistre la configuration CORS pour toutes les URL (/**) dans la source de configuration.
        source.registerCorsConfiguration("/**", config);

        // Retourne une nouvelle instance de CorsFilter configurée avec la source définie.
        return new CorsFilter(source);
    }


}
/*
 * Explication des concepts :
 *
 * UserDetailsService :
 * UserDetailsService est une interface fournie par Spring Security qui est utilisée
 * pour récupérer les détails de l'utilisateur. Elle a une méthode principale :
 * UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
 * Cette méthode est utilisée par Spring Security pour charger les informations de l'utilisateur
 * (comme le nom d'utilisateur, le mot de passe et les rôles) à partir d'une source de données
 * (par exemple, une base de données).
 *
 * AuthenticationProvider :
 * AuthenticationProvider est une interface de Spring Security qui définit comment un utilisateur
 * est authentifié. Elle a une méthode principale :
 * Authentication authenticate(Authentication authentication) throws AuthenticationException;
 * Cette méthode prend en entrée les informations d'authentification (comme le nom d'utilisateur et
 * le mot de passe) et retourne un objet Authentication complet si l'authentification réussit.
 *
 * DaoAuthenticationProvider :
 * DaoAuthenticationProvider est une implémentation de l'interface AuthenticationProvider qui utilise
 * un UserDetailsService pour récupérer les informations de l'utilisateur et un PasswordEncoder pour
 * vérifier le mot de passe. Voici ses principales responsabilités :
 *  - Récupérer les détails de l'utilisateur : Il utilise le UserDetailsService configuré pour charger
 *    les informations de l'utilisateur par son nom d'utilisateur.
 *  - Vérifier le mot de passe : Il utilise le PasswordEncoder configuré pour comparer le mot de passe
 *    fourni avec le mot de passe stocké.
 */

/*
@Bean
public CorsFilter corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    // Configuration pour les API publiques
    final CorsConfiguration publicConfig = new CorsConfiguration();
    publicConfig.setAllowedOrigins(Collections.singletonList("*")); // Permet toutes les origines
    publicConfig.setAllowedMethods(Arrays.asList("GET", "POST"));
    source.registerCorsConfiguration("/api/public/**", publicConfig);

    // Configuration pour les API privées
    final CorsConfiguration privateConfig = new CorsConfiguration();
    privateConfig.setAllowedOrigins(Collections.singletonList("https://trustedorigin.com")); // Permet uniquement une origine spécifique
    privateConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    privateConfig.setAllowCredentials(true);
    source.registerCorsConfiguration("/api/private/**", privateConfig);

    return new CorsFilter(source);
}

 */