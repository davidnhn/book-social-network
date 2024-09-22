package com.davidnhn.book.book;

import com.davidnhn.book.common.PageResponse;
import com.davidnhn.book.exceptions.OperationNotPermittedException;
import com.davidnhn.book.file.FileStorageService;
import com.davidnhn.book.history.BookTransactionHistory;
import com.davidnhn.book.history.BookTransactionHistoryRepository;
import com.davidnhn.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);

        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("Book with id : " + bookId + " not found "));
    }

    /**
     * Récupère une page de livres affichables pour l'utilisateur connecté.
     *
     * @param page Le numéro de la page à récupérer.
     * @param size Le nombre de livres par page.
     * @param connectedUser L'objet Authentication représentant l'utilisateur actuellement connecté.
     * @return Une réponse paginée contenant les livres et les informations de pagination.
     */
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());

        // Crée un objet Pageable pour la pagination, trié par date de création décroissante
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Récupère la page de livres affichables pour l'utilisateur connecté
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());

        // Convertit les entités Book en BookResponse
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponse, // Liste des réponses de livres
                books.getNumber(), // Numéro de la page actuelle
                books.getSize(), // Taille de la page (nombre d'éléments par page)
                books.getTotalPages(), // Nombre total de pages disponibles
                books.getTotalElements(), // Nombre total d'éléments disponibles
                books.isFirst(), // Indique si c'est la première page
                books.isLast() // Indique si c'est la dernière page

        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);

        // Convertit les entités Book en BookResponse
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponse, // Liste des réponses de livres
                books.getNumber(), // Numéro de la page actuelle
                books.getSize(), // Taille de la page (nombre d'éléments par page)
                books.getTotalPages(), // Nombre total de pages disponibles
                books.getTotalElements(), // Nombre total d'éléments disponibles
                books.isFirst(), // Indique si c'est la première page
                books.isLast() // Indique si c'est la dernière page

        );

    }

public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
    User user = ((User) connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
    List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
            .map(bookMapper::toBorrowedBookResponse)
            .toList();

    return new PageResponse<>(
            bookResponse, // Liste des réponses de livres
            allBorrowedBooks.getNumber(), // Numéro de la page actuelle
            allBorrowedBooks.getSize(), // Taille de la page (nombre d'éléments par page)
            allBorrowedBooks.getTotalPages(), // Nombre total de pages disponibles
            allBorrowedBooks.getTotalElements(), // Nombre total d'éléments disponibles
            allBorrowedBooks.isFirst(), // Indique si c'est la première page
            allBorrowedBooks.isLast() // Indique si c'est la dernière page

    );

}

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allReturnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponse, // Liste des réponses de livres
                allReturnedBooks.getNumber(), // Numéro de la page actuelle
                allReturnedBooks.getSize(), // Taille de la page (nombre d'éléments par page)
                allReturnedBooks.getTotalPages(), // Nombre total de pages disponibles
                allReturnedBooks.getTotalElements(), // Nombre total d'éléments disponibles
                allReturnedBooks.isFirst(), // Indique si c'est la première page
                allReturnedBooks.isLast() // Indique si c'est la dernière page

        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id : " + bookId + " not found "));

        User user = ((User) connectedUser.getPrincipal());

        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update other books shareable status");
        }

        book.setShareable(!book.isShareable());
        bookRepository.save(book);

        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id : " + bookId + " not found "));

        User user = ((User) connectedUser.getPrincipal());

        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update other books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);

        return bookId;
    }


    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id : " + bookId + " not found "));

        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot borrow this Book. It is archived or not shareable");
        }

        User user = ((User) connectedUser.getPrincipal());

        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow this Book if you are the owner of the book");
        }

        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if(isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();

        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id : " + bookId + " not found "));

        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot return this Book. It is archived or not shareable");
        }

        User user = ((User) connectedUser.getPrincipal());

        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot return this Book if you are the owner of the book");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));

        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();

    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));

        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
}

/*
 * Explications détaillées
 *
 * Pageable et Pagination :
 * - Pageable est un objet utilisé pour encapsuler les informations de pagination et de tri.
 *   Il est passé à la méthode du dépôt findAllDisplayableBooks pour spécifier quelle page
 *   de résultats doit être récupérée et comment ces résultats doivent être triés.
 * - Le framework Spring Data JPA utilise cet objet pour limiter le nombre de résultats
 *   retournés par la base de données, ce qui est essentiel pour la pagination efficace,
 *   surtout lorsqu'il y a de nombreux enregistrements.
 *
 * Stream et Page :
 * - Oui, vous pouvez utiliser stream() sur un objet de type Page. Page étend Slice, qui étend
 *   Streamable, ce qui permet d'obtenir un flux (Stream) de ses éléments.
 * - Utiliser stream() permet de traiter les éléments de la page de manière fonctionnelle,
 *   comme le mapping des entités Book en objets BookResponse dans cet exemple.
 *
 * Méthodes de Page :
 * - books.getNumber(): Retourne le numéro de la page actuelle (zéro-based).
 * - books.getSize(): Retourne le nombre d'éléments par page.
 * - books.getTotalPages(): Retourne le nombre total de pages disponibles.
 * - books.getTotalElements(): Retourne le nombre total d'éléments disponibles.
 * - books.isFirst(): Indique si c'est la première page.
 * - books.isLast(): Indique si c'est la dernière page.
 *
 *
 * Raisons pours lesquelles il est préférable de créer PageResponse et ne pas utiliser Page directement
 *
 * 1. Abstraction et Encapsulation :
 * - Séparation des préoccupations : En encapsulant la réponse de pagination dans une classe
 *   spécifique à votre application, vous découplez votre logique de service de la logique
 *   spécifique à Spring Data. Cela permet de mieux gérer les modifications futures dans
 *   votre logique de pagination.
 * - Abstraction : Vous abstraisez les détails de l'implémentation de Spring Data, ce qui
 *   permet de changer la bibliothèque ou la stratégie de pagination sans modifier l'API
 *   publique.
 *
 * 2. Simplicité et Clarté :
 * - Simplicité : Une classe PageResponse personnalisée peut être plus simple à comprendre et
 *   à utiliser pour les développeurs travaillant sur le front-end, car elle expose uniquement
 *   les informations nécessaires de manière claire et concise.
 * - Clarté : En utilisant une classe personnalisée, vous pouvez renommer les champs et les
 *   méthodes pour qu'ils correspondent mieux à votre domaine métier, améliorant ainsi la
 *   lisibilité et la compréhension du code.
 *
 * 3. Ajout de Métadonnées ou d'Informations Supplémentaires :
 * - Métadonnées supplémentaires : Vous pouvez facilement ajouter des champs supplémentaires à
 *   votre PageResponse pour inclure des métadonnées spécifiques à votre application, telles
 *   que des messages d'erreur, des indicateurs spécifiques, des liens de navigation, etc.
 * - Flexibilité : Même si vous n'ajoutez pas de méthodes ou de champs supplémentaires maintenant,
 *   avoir une classe personnalisée vous donne la flexibilité de le faire à l'avenir sans modifier
 *   beaucoup de code existant.
 *
 * 4. Personnalisation et Contrôle :
 * - Personnalisation de la sérialisation : En utilisant une classe personnalisée, vous avez un
 *   contrôle total sur la sérialisation JSON ou XML, ce qui peut être utile pour des raisons de
 *   performance ou de compatibilité.
 * - Validations spécifiques : Vous pouvez ajouter des validations spécifiques à votre classe
 *   de réponse si nécessaire.
 */
