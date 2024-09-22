package com.davidnhn.book.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Annotation Lombok qui génère un constructeur avec tous les champs finaux
@RequiredArgsConstructor
// Indique que cette classe est un service Spring
@Component
// Cette classe étend OncePerRequestFilter pour s'assurer qu'elle s'exécute une seule fois par requête
public class JwtFilter extends OncePerRequestFilter {

    // Injection de dépendances via le constructeur généré par @RequiredArgsConstructor
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    // Cette méthode s'exécute pour chaque requête HTTP passant par le filtre
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Si la requête concerne l'authentification, on passe directement au filtre suivant
        if(request.getServletPath().contains("/api/v1/auth")){
            filterChain.doFilter(request, response);
            return;
        }

        // Récupération de l'en-tête d'autorisation
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;

        // Si l'en-tête d'autorisation est absent ou mal formé, on passe au filtre suivant
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token JWT (en enlevant "Bearer ")
        jwt = authHeader.substring(7);
        // Extraction de l'email de l'utilisateur à partir du token
        userEmail = jwtService.extractUsername(jwt); // username est l'email dans notre application

        // Si l'email est présent et que l'utilisateur n'est pas encore authentifié
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // Chargement des détails de l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            // Vérification de la validité du token
            if(jwtService.isTokenValid(jwt, userDetails)){
                // Création d'un token d'authentification
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Pas de credentials car l'authentification est basée sur le JWT
                        userDetails.getAuthorities() // Les autorités de l'utilisateur
                );

                // Ajout des détails de la requête à l'authentification
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Mise à jour du contexte de sécurité avec le nouveau token d'authentification
                SecurityContextHolder.getContext().setAuthentication(authToken);
                // les filtres d'authentification suinvant JwTFilter seront ignoré par spring
            }
        }

        // Passage au filtre suivant dans la chaîne
        filterChain.doFilter(request, response);
    }
}

/*
 * Utilisation de champs finaux et de @RequiredArgsConstructor :
 *
 * 1. Champs finaux (jwtService et userDetailsService) :
 *    a) Immuabilité : Une fois initialisés, ces champs ne peuvent plus être modifiés,
 *       garantissant que ces dépendances restent constantes tout au long de la vie
 *       de l'objet JwtFilter. C'est une bonne pratique pour la sécurité et la
 *       prévisibilité du comportement.
 *    b) Thread-safety : Les objets immuables sont intrinsèquement thread-safe,
 *       ce qui aide à prévenir les problèmes de concurrence dans un environnement
 *       multi-threads comme un serveur web.
 *    c) Design clair : L'utilisation de 'final' indique clairement que ces dépendances
 *       sont essentielles au fonctionnement de la classe et doivent être fournies
 *       à la création de l'objet.
 *
 * 2. @RequiredArgsConstructor :
 *    a) Injection de dépendances : Cette annotation génère automatiquement un constructeur
 *       avec tous les champs 'final' comme paramètres, permettant à Spring d'injecter
 *       automatiquement les dépendances lors de la création du bean JwtFilter.
 *    b) Réduction du boilerplate : Évite d'avoir à écrire manuellement un constructeur.
 *    c) Couplage avec l'injection de dépendances : Assure que toutes les dépendances
 *       nécessaires sont fournies lors de la création de l'objet, prévenant les erreurs
 *       potentielles à l'exécution.
 *    d) Flexibilité : Facilite l'ajout ou la suppression de dépendances dans le futur,
 *       le constructeur étant automatiquement mis à jour.
 */

/*
 * Injection de UserDetailsService :
 *
 * 1. Utilisation de l'interface UserDetailsService plutôt que de l'implémentation spécifique UserDetailsServiceImpl :
 *    - Suit le principe de programmation orientée interface.
 *    - Permet un couplage faible, rendant le code plus flexible pour d'éventuelles modifications futures.
 *    - Suffisant pour utiliser la méthode loadUserByUsername(), qui est définie dans l'interface.
 *
 * 2. Spring injectera automatiquement l'implémentation UserDetailsServiceImpl :
 *    - Tant qu'il n'y a qu'une seule implémentation de UserDetailsService dans le contexte Spring.
 *    - Pas besoin de spécifier explicitement UserDetailsServiceImpl.
 *
 * 3. Avantages :
 *    - Facilite les tests unitaires (possibilité de mock plus facilement).
 *    - Permet de changer d'implémentation sans modifier cette classe.
 *    - Respecte le principe de "programmer vers une interface, pas une implémentation".
 *
 * Note : Si des méthodes spécifiques à UserDetailsServiceImpl étaient nécessaires (ce qui n'est pas le cas ici),
 * l'injection de l'implémentation spécifique serait alors justifiée.
 */