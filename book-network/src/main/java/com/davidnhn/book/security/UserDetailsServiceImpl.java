package com.davidnhn.book.security;

import com.davidnhn.book.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional // charge role/authorities avec
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

/*
 * Utilisation de @Transactional :
 *
 * 1. Cohérence des Données :
 *    - Même avec le chargement anticipé (eager loading) des relations,
 *      l'utilisation de @Transactional garantit que toutes les opérations
 *      de lecture sont exécutées de manière atomique et cohérente.
 *
 * 2. Gestion des Exceptions :
 *    - Si une exception survient pendant l'exécution de la méthode, le conteneur
 *      de transaction peut gérer correctement le rollback (annulation) des opérations
 *      en cours, ce qui est important même pour des opérations de lecture.
 *
 * 3. Chargement paresseux (Lazy Loading) :
 *    - Si vous utilisez le chargement paresseux pour certaines relations, @Transactional
 *      garantit que ces relations sont correctement chargées dans le contexte de la transaction,
 *      évitant les LazyInitializationException.
 *
 * 4. Future Extensibilité :
 *    - Ajouter @Transactional vous donne la flexibilité de changer la stratégie de chargement
 *      ou d'ajouter d'autres opérations dans la méthode sans avoir à vous soucier de la gestion
 *      explicite des transactions à ce moment-là.
 *
 * En résumé, même avec le chargement anticipé des rôles, l'utilisation de @Transactional
 * dans la méthode loadUserByUsername assure la cohérence des données et prépare le terrain
 * pour une éventuelle évolutivité ou modification future de la méthode.
 *
 *      Stratégies de chargement par défaut :
 * - @OneToMany et @ManyToMany : LAZY
 * - @ManyToOne et @OneToOne   : EAGER
 *
 *  * En résumé, même avec le chargement anticipé des rôles, l'utilisation de @Transactional
 * dans la méthode loadUserByUsername assure la cohérence des données et prépare le terrain
 * pour une éventuelle évolutivité ou modification future de la méthode.
 *
 * Problèmes potentiels avec Lazy Loading et @Transactional :
 * - Si vous accédez à une relation paresseuse en dehors d'une transaction, une LazyInitializationException est levée.
 * - Avec @Transactional, la session de persistance reste ouverte pendant toute la durée de la transaction,
 *   permettant ainsi le chargement paresseux des relations sans lever d'exception.
 * - Sans @Transactional, la session de persistance est fermée dès que l'opération de base de données est terminée,
 *   ce qui provoque des LazyInitializationException si vous essayez d'accéder à des relations paresseuses.
 *
 * En gros quand c'est lazy ca charge les relations seulement quand ont y fait appel , par quand on recupere l'element.
 * Si on récupere un objet et qui des relation en lazy :
 * On utilise le repository pour recupere l'objet (sans ses relation car on est en lazy)
 * Puis recupere les relations de l'objet mais la session de persistence estr fermée donc les relation ne charge pas et ca leve un exception lazyness
 * Avec Transactionnal, la session de persistence reste ouverte on peut acceder a la bdd et recuperer les relation de l'objet apres l'avoir fetch avec le repository
 */