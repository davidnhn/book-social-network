package com.davidnhn.book.role;

import com.davidnhn.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users;
    /*
     * Relation ManyToMany avec User :
     * - Plusieurs rôles peuvent être attribués à plusieurs utilisateurs, et vice versa.
     * - Cette classe (Role) est le côté inverse de la relation, indiqué par 'mappedBy = "roles"'.
     * - Étant le côté inverse, les modifications de la relation se font via l'entité User.
     * - Le choix de faire de Role le côté inverse implique qu'il est plus fréquent de modifier
     *   les rôles d'un utilisateur que les utilisateurs d'un rôle.
     */

    @CreatedDate
    @Column(nullable = false,updatable=false)
    private LocalDateTime createdDate;
    @LastModifiedDate //  mis à jour automatiquement chaque fois que l'entité est modifiée.
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}
