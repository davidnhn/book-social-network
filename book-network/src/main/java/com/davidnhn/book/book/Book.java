package com.davidnhn.book.book;

import com.davidnhn.book.common.BaseEntity;
import com.davidnhn.book.feedback.Feedback;
import com.davidnhn.book.history.BookTransactionHistory;
import com.davidnhn.book.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name= "owner_id") // optionnel, spring le fait tout seul
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;



    /**
     * @Transient indique à JPA que cet attribut ne doit pas être persistant.

     * @Transient est utilisé pour marquer un champ d'une entité qui ne doit pas être mappé à une colonne dans la base de données.
     * Les champs annotés avec @Transient sont ignorés par l'ORM lors de l'insertion, de la mise à jour et de la récupération des entités.

     * Dans cet exemple, le champ rate n'est pas persistant car il est calculé à la volée en fonction des feedbacks.
     * Cela évite de stocker des données redondantes et garantit que la valeur est toujours à jour.
     */
    @Transient
    public double getRate() {
        if(feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }

        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);

        double roundedRate = Math.round(rate * 10.0) / 10.0;
        return roundedRate;
    }
}

/*
 * mapToDouble(Feedback::getNote) : Bien que getNote retourne un double, mapToDouble est utilisé pour
 * convertir chaque élément du flux en un double primitif. Cela crée un DoubleStream, qui a des opérations
 * spécialisées pour les types primitifs, comme average(). Utiliser map directement ici ne fonctionnerait pas
 * parce que map retourne un Stream<Double>, qui ne possède pas la méthode average(). En utilisant mapToDouble,
 * nous obtenons un DoubleStream, ce qui permet de calculer directement la moyenne des doubles.
 */

/*
 * Arrondit la valeur de 'rate' à une décimale.

 * 1. Multiplication par 10.0 : Cela déplace la décimale d'une position vers la droite.
 *    Exemple : Si 'rate' est 2.345, alors 'rate * 10.0' devient 23.45.

 * 2. Arrondi à l'entier le plus proche : Math.round(rate * 10.0)
 *    Cela arrondit la valeur à l'entier le plus proche.
 *    Exemple : Math.round(23.45) donne 23, car 23.45 est plus proche de 23 que de 24.

 * 3. Division par 10.0 : Cela ramène la décimale à sa position originale mais arrondie à une décimale.
 *    Exemple : 23 / 10.0 donne 2.3.

 * L'objectif de cette ligne est de fournir une version arrondie de 'rate' avec une seule décimale pour une
 * meilleure lisibilité ou pour respecter des exigences spécifiques d'affichage.
 */

