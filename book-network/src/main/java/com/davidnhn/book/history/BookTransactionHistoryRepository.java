package com.davidnhn.book.history;

import com.davidnhn.book.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
           SELECT history
           FROM BookTransactionHistory history
           WHERE history.user.id = :userId
           """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable,@Param("userId") Integer userId);

    @Query("""
           SELECT history
           FROM BookTransactionHistory history
           WHERE history.book.owner.id = :userId
           """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, @Param("userId") Integer userId);

    /**
     * Cette méthode vérifie si un livre est déjà emprunté par un utilisateur spécifique et si le retour n'a pas encore été approuvé.
     *
     * @param bookId L'ID du livre à vérifier.
     * @param userId L'ID de l'utilisateur à vérifier.
     * @return Un booléen indiquant si le livre est déjà emprunté par l'utilisateur.
     */
        @Query("""
                SELECT
                (COUNT (*) > 0) AS isBorrowed
                FROM BookTransactionHistory bookTransactionHistory
                WHERE bookTransactionHistory.user.id = :userId
                AND bookTransactionHistory.book.id = :bookId
                AND bookTransactionHistory.returnApproved = false
              """)
        boolean isAlreadyBorrowedByUser(@Param("bookId") Integer bookId, @Param("userId") Integer userId);

        @Query("""
                SELECT transaction
                FROM BookTransactionHistory transaction
                WHERE transaction.user.id = :userId
                AND transaction.book.id = :bookId
                AND transaction.returned = false
                AND transaction.returnApproved = false
                """)
        Optional<BookTransactionHistory> findByBookIdAndUserId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);

        @Query("""
               SELECT transaction
               FROM BookTransactionHistory transaction
               WHERE transaction.book.id = :bookId
               AND transaction.book.owner.id = :ownerId
               AND transaction.returned = true
               AND transaction.returnApproved = false
               """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(@Param("bookId") Integer bookId, @Param("ownerId") Integer ownerId);
}


