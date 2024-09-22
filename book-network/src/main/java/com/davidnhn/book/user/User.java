package com.davidnhn.book.user;

import com.davidnhn.book.book.Book;
import com.davidnhn.book.history.BookTransactionHistory;
import com.davidnhn.book.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "_user") // car user est un nom reservé
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    @Column(unique=true)
    private String email;
    private String password;
    private boolean accountLocked;
    private boolean enabled;


    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
    /*
     * Relation ManyToMany avec Role :
     * - Plusieurs utilisateurs peuvent avoir plusieurs rôles, et vice versa.
     * - Cette classe (User) est le côté propriétaire de la relation.
     * - Généralement, on choisit le côté "propriétaire" comme étant celui qui sera le plus souvent modifié ou interrogé. Dans ce cas, il est plus probable qu'on modifie les rôles d'un utilisateur plutôt que les utilisateurs d'un rôle.
     * - FetchType.EAGER signifie que les rôles seront automatiquement chargés avec l'utilisateur.
     *   Avantage : Accès immédiat aux rôles.
     *   Inconvénient : Peut impacter les performances si on charge beaucoup d'utilisateurs.
     */

    @OneToMany(mappedBy = "owner")
    private List<Book> books;

    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistory> histories;

    @CreatedDate
    @Column(nullable = false,updatable=false)
    private LocalDateTime createdDate;
    @LastModifiedDate //  mis à jour automatiquement chaque fois que l'entité est modifiée.
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    /*
    * @CreatedDate : Cette annotation de Spring Data indique que ce champ doit être automatiquement rempli avec la date et l'heure de création de l'entité.
        @Column : Cette annotation JPA permet de spécifier les détails de la colonne dans la base de données.
            nullable = false : Signifie que cette colonne ne peut pas contenir de valeurs NULL. La base de données refusera d'insérer ou de mettre à jour une ligne si ce champ est vide.
            updatable = false : Indique que cette colonne ne peut pas être mise à jour une fois qu'elle a été initialisée. Cela garantit que la date de création ne change jamais après la première insertion.

      @LastModifiedDate : Cette annotation de Spring Data indique que ce champ doit être automatiquement mis à jour avec la date et l'heure actuelles chaque fois que l'entité est modifiée.
        @Column(insertable = false) :
            insertable = false : Cela signifie que cette colonne ne sera pas incluse dans les requêtes SQL INSERT générées par JPA. En d'autres termes, lors de la création initiale de l'entité, ce champ ne sera pas défini. Il ne sera mis à jour que lors des opérations de modification ultérieures.
      */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return email;
    }

    public String fullName() {
        return firstname + " " + lastname;
    }
}
