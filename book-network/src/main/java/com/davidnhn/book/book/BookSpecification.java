package com.davidnhn.book.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
/*
 * Explication détaillée de l'utilisation de Specification dans Spring Boot

 * Spring Data JPA offre une API puissante et flexible pour construire des requêtes dynamiques
 * via les Specification. Les spécifications permettent de créer des critères de requête de
 * manière programmatique et de les combiner pour obtenir des requêtes complexes. Voici une
 * explication détaillée de leur utilisation et des concepts associés :
 *
 * 1. L'interface Specification
 * L'interface Specification fait partie de Spring Data JPA et permet de définir des critères
 * de requête de manière déclarative. Elle est définie comme suit :
 *
 * public interface Specification<T> {
 *     Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);
 * }
 *
 * - T : Le type de l'entité sur laquelle la spécification opère.
 * - toPredicate : Méthode principale qui doit être implémentée pour définir les critères de la requête.
 *
 * 2. Les Paramètres de toPredicate
 * - Root<T> root : Représente la racine de l'entité dans la requête. Il permet d'accéder aux propriétés de l'entité.
 * - CriteriaQuery<?> query : Représente la requête elle-même. Il permet de personnaliser la requête, par exemple en ajoutant des sélections ou des ordres.
 * - CriteriaBuilder criteriaBuilder : Fournit des méthodes pour construire des prédicats (conditions de requête).
 *   C'est l'API principale pour créer des expressions de critères.
 *
 * - root.get("owner").get("id") : Accède à l'attribut id de l'entité owner associée à l'entité Book.
 * - criteriaBuilder.equal(...) : Crée un prédicat qui vérifie l'égalité entre l'ID du propriétaire et la valeur fournie.
 *


 * 6. Concepts Clés
 * - root : Représente la table principale de la requête. Utilisé pour naviguer dans les propriétés de l'entité.
 * - query : Représente la requête en cours de construction. Utilisé pour ajouter des sélections, des groupements, etc.
 * - criteriaBuilder : Fournit des méthodes pour construire des expressions et des prédicats. Utilisé pour créer des conditions de type where, join, order by, etc.
 *
 * 7. Utilisations Courantes
 * - Filtrage simple : Égalité, inégalité, etc.
 *
 * public static Specification<Book> withTitle(String title) {
 *     return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("title"), title);
 * }
 *
 * - Filtrage complexe : Combinaison de plusieurs critères.
 *
 * public static Specification<Book> withComplexCriteria(String title, Integer ownerId) {
 *     return (root, query, criteriaBuilder) -> criteriaBuilder.and(
 *         criteriaBuilder.equal(root.get("title"), title),
 *         criteriaBuilder.equal(root.get("owner").get("id"), ownerId)
 *     );
 * }
 *
 * - Joins : Naviguer dans les associations pour appliquer des filtres.
 *
 * public static Specification<Book> withOwnerName(String ownerName) {
 *     return (root, query, criteriaBuilder) -> {
 *         Join<Book, User> owner = root.join("owner");
 *         return criteriaBuilder.equal(owner.get("name"), ownerName);
 *     };
 * }
 */
