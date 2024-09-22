package com.davidnhn.book.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Id
    @GeneratedValue
    private Integer id;

    // auditing fields

    /**
     * @CreatedDate indique que ce champ doit être automatiquement rempli avec la date de création de l'entité.
     * @Column(nullable = false, updatable = false) précise que ce champ ne peut pas être nul et ne doit pas être mis à jour une fois défini.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * @LastModifiedDate indique que ce champ doit être automatiquement mis à jour avec la dernière date de modification de l'entité.
     * @Column(insertable = false) précise que ce champ ne doit pas être insérable, c'est-à-dire qu'il ne doit pas être défini lors de l'insertion initiale de l'entité.
     */
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    /**
     * @CreatedBy indique que ce champ doit être automatiquement rempli avec l'identifiant de l'utilisateur qui a créé l'entité.
     * @Column(nullable = false, updatable = false) précise que ce champ ne peut pas être nul et ne doit pas être mis à jour une fois défini.
     */
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Integer createdBy;

    /**
     * @LastModifiedBy indique que ce champ doit être automatiquement mis à jour avec l'identifiant de l'utilisateur qui a modifié l'entité en dernier.
     * @Column(insertable = false) précise que ce champ ne doit pas être insérable, c'est-à-dire qu'il ne doit pas être défini lors de l'insertion initiale de l'entité.
     */
    @LastModifiedBy
    @Column(insertable = false)
    private Integer lastModifiedBy;

}


/*
 * @MappedSuperclass : Cette annotation indique que la classe est une superclasse dont les propriétés doivent être héritées par les classes entités.
 * Elle ne sera pas mappée à une table de base de données par elle-même, mais ses champs seront inclus dans les entités qui l'étendent.
 */


// L'audit ne connait que createddate et lastmodifieddate , pour les autre champs on doit creer ApplicationAuditAware