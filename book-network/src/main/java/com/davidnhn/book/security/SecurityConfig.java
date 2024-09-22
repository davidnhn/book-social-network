package com.davidnhn.book.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // pour les attributs en final
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())// withdefault() va utiliser le bean qui retourne un CorsFilter
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(
                                "/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html" // c'est la route a utiliser pour afficher le swagger http://localhost:8088/api/v1/swagger-ui/index.html
                        ).permitAll() // Autorise toutes les requêtes vers les endpoints spécifiés sans authentification
                                .anyRequest() // Toutes les autres requêtes
                                    .authenticated() // Nécessitent une authentification
                        )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider) // Définit le fournisseur d'authentification à utiliser
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}

/*
 * .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
 * Cette ligne ajoute le filtre jwtAuthFilter dans la chaîne de filtres de Spring Security.
 * Elle spécifie que jwtAuthFilter doit être exécuté avant UsernamePasswordAuthenticationFilter.
 * Cela signifie que les requêtes sont d'abord vérifiées pour la présence et la validité d'un token JWT.
 * Si un token JWT valide est trouvé, le contexte de sécurité est configuré avec les détails de l'utilisateur.
 * Cela rend inutile l'exécution de UsernamePasswordAuthenticationFilter pour les requêtes déjà authentifiées par JWT.
 *  * Si un token JWT valide est trouvé, le contexte de sécurité est configuré avec les détails de l'utilisateur.
 * Si l'utilisateur est authentifié avec succès via le token JWT, le contexte de sécurité est mis à jour
 * avec un objet Authentication valide. Les autres filtres d'authentification, tels que
 * UsernamePasswordAuthenticationFilter, vérifient d'abord le contexte de sécurité pour voir
 * s'il contient déjà un objet Authentication. Si c'est le cas, ils n'essaieront pas de réauthentifier
 * l'utilisateur et continueront simplement la chaîne de filtres
 */

/**
 * Configuration de la sécurité et explication du processus d'authentification.

 * Cette classe configure la sécurité de l'application en utilisant Spring Security.
 * Elle définit un filtre de sécurité, un fournisseur d'authentification, et un encodeur de mots de passe.

 * Processus d'authentification :
 * 1. Requête d'authentification :
 *    - Lorsqu'une requête d'authentification est envoyée (par exemple, via /login), Spring Security intercepte cette requête via un filtre d'authentification (UsernamePasswordAuthenticationFilter).

 * 2. Chargement de l'utilisateur :
 *    - Le DaoAuthenticationProvider utilise la classe UserDetailsServiceImpl pour charger les détails de l'utilisateur (y compris le mot de passe chiffré) à partir de la base de données.

 * 3. Comparaison des mots de passe :
 *    - Le DaoAuthenticationProvider utilise le PasswordEncoder configuré (par exemple, BCryptPasswordEncoder) pour comparer le mot de passe fourni dans la requête avec le mot de passe chiffré récupéré.
 *    - Si les mots de passe correspondent, l'utilisateur est authentifié avec succès. Sinon, l'authentification échoue.

 * Configuration des composants :
 * - SecurityConfig : Configure les paramètres de sécurité, y compris le filtre JWT, le fournisseur d'authentification, et le gestionnaire de sessions.
 * - UserDetailsServiceImpl : Implémente UserDetailsService pour charger les détails de l'utilisateur à partir de la base de données.
 * - UserService : Gère l'encodage des mots de passe lors de l'enregistrement des utilisateurs.
 */
