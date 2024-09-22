package com.davidnhn.book.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    /**
     * Récupère une page de livres affichables pour l'utilisateur connecté.
     * Les livres affichables sont ceux qui ne sont pas archivés, qui sont partageables,
     * et qui n'appartiennent pas à l'utilisateur connecté.
     *
     * @param pageable Un objet Pageable qui spécifie la pagination et le tri.
     * @param userId L'ID de l'utilisateur connecté.
     * @return Une page de livres affichables.
     */
    @Query("""
           SELECT book
           FROM Book book
           WHERE book.archived = false
           AND book.shareable = true
           AND book.owner.id != :userId
           """)
    Page<Book> findAllDisplayableBooks(Pageable pageable,@Param("userId") Integer userId);


}


/*
 * Explication détaillée de l'utilisation de @Query et JPQL dans Spring Boot
 *
 * Spring Data JPA offre une API puissante et flexible pour construire des requêtes dynamiques
 * via les annotations @Query. Les spécifications permettent de créer des critères de requête
 * de manière programmatique et de les combiner pour obtenir des requêtes complexes. Voici une
 * explication détaillée de leur utilisation et des concepts associés :
 *
 * 1. L'annotation @Query :
 * - @Query permet de définir des requêtes JPQL ou SQL natives directement dans les interfaces de repository.
 * - Exemple de base :
 *   @Query("SELECT u FROM User u WHERE u.email = :email")
 *   User findByEmail(@Param("email") String email);
 * - JPQL fonctionne sur les entités JPA plutôt que sur les tables de la base de données.
 * - Utilisation des paramètres nommés avec @Param.
 *
 * 2. Paramètres de @Query :
 * - Paramètres nommés : Utilisation des paramètres précédés de : et liés à la méthode avec @Param.
 *   @Query("SELECT u FROM User u WHERE u.email = :email")
 *   User findByEmail(@Param("email") String email);
 * - Paramètres indexés : Utilisation des paramètres avec des positions numériques.
 *   @Query("SELECT u FROM User u WHERE u.email = ?1")
 *   User findByEmail(String email);
 *
 * 3. Requêtes JPQL :
 * - Sélection de champs spécifiques :
 *   @Query("SELECT u.email, u.name FROM User u WHERE u.active = true")
 *   List<Object[]> findActiveUserEmailsAndNames();
 * - Requêtes avec jointures :
 *   @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
 *   List<User> findByRoleName(@Param("roleName") String roleName);
 * - Requêtes avec agrégations :
 *   @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
 *   Long countActiveUsers();
 * - Requêtes avec sous-requêtes :
 *   @Query("SELECT u FROM User u WHERE u.department.id IN (SELECT d.id FROM Department d WHERE d.name = :deptName)")
 *   List<User> findByDepartmentName(@Param("deptName") String deptName);
 *
 * 4. Requêtes natives :
 * - Utilisation des requêtes SQL natives pour des opérations spécifiques à la base de données.
 *   @Query(value = "SELECT * FROM users u WHERE u.email = :email", nativeQuery = true)
 *   User findByEmailNative(@Param("email") String email);
 *
 * 5. Requêtes dynamiques avec Specifications :
 * - Les Specifications permettent de créer des critères de requête dynamiques et complexes.
 * - Exemple :
 *   public class UserSpecification {
 *       public static Specification<User> hasEmail(String email) {
 *           return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
 *       }
 *       public static Specification<User> isActive() {
 *           return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("active"));
 *       }
 *   }
 * - Utilisation dans le repository :
 *   @Repository
 *   public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {}
 * - Utilisation dans le service :
 *   Specification<User> spec = Specification.where(UserSpecification.hasEmail("example@example.com"))
 *                                           .and(UserSpecification.isActive());
 *   List<User> users = userRepository.findAll(spec);
 *
 * 6. Utilisation avancée :
 * - Pagination et tri :
 *   @Query("SELECT u FROM User u WHERE u.active = true")
 *   Page<User> findActiveUsers(Pageable pageable);
 * - Expressions SpEL (Spring Expression Language) :
 *   @Query("SELECT u FROM #{#entityName} u WHERE u.email = :email")
 *   User findByEmailSpel(@Param("email") String email);
 * - Modifications et suppressions :
 *   @Modifying
 *   @Query("UPDATE User u SET u.active = false WHERE u.id = :id")
 *   int deactivateUser(@Param("id") Integer id);
 *   @Modifying
 *   @Query("DELETE FROM User u WHERE u.id = :id")
 *   int deleteUserById(@Param("id") Integer id);
 *
 * 7. Bonnes pratiques :
 * - Valider les requêtes : Utiliser les tests unitaires pour valider que vos requêtes fonctionnent comme prévu.
 * - Utiliser les paramètres nommés : Préférer les paramètres nommés pour des requêtes plus lisibles et maintenables.
 * - Limiter l'utilisation des requêtes natives : Utiliser les requêtes natives seulement lorsque nécessaire.
 * - Paginer les résultats : Utiliser la pagination pour les requêtes retournant potentiellement de grandes quantités de données.
 * - Combiner les Specifications : Utiliser les Specifications pour construire des requêtes dynamiques et combinables.
 *
 * Conclusion :
 * L'annotation @Query et les spécifications JPQL de Spring Data JPA offrent une grande flexibilité pour construire des
 * requêtes complexes de manière déclarative. Elles permettent de gérer la plupart des cas pratiques que vous rencontrerez
 * au travail, de la simple récupération de données aux opérations de modification complexes, en passant par la pagination
 * et le tri. Utiliser ces outils efficacement peut grandement améliorer la maintenabilité et la performance de votre application.
 */
