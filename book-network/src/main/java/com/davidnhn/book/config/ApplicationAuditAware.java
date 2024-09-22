package com.davidnhn.book.config;

import com.davidnhn.book.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
// l'id de user est un integer
public class ApplicationAuditAware implements AuditorAware<Integer> {

    /**
     * Cette méthode est utilisée pour obtenir l'ID de l'utilisateur actuellement authentifié,
     * ce qui est nécessaire pour l'audit des entités (par exemple, pour remplir les champs
     * @CreatedBy et @LastModifiedBy automatiquement).
     *
     * @return Un Optional contenant l'ID de l'utilisateur actuellement authentifié, ou un Optional.empty() si l'utilisateur n'est pas authentifié.
     */
    @Override
    public Optional<Integer> getCurrentAuditor() {
        // Récupère l'objet Authentication représentant l'utilisateur actuellement authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();// Retourne un Optional vide si l'utilisateur n'est pas authentifié
        }

        // Récupère l'objet principal de l'authentification, qui devrait être une instance de User
        User userPrincipal = (User) authentication.getPrincipal();

        // Retourne l'ID de l'utilisateur actuellement authentifié
        return Optional.ofNullable(userPrincipal.getId());
    }
}
