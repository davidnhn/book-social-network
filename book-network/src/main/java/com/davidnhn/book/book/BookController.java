package com.davidnhn.book.book;

import com.davidnhn.book.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name="Book")
public class BookController {


    private final BookService service;

    @PostMapping
    public ResponseEntity<Integer> saveBook(@Valid @RequestBody BookRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Integer bookId) {
        return ResponseEntity.ok(service.findById(bookId));
    }



    @GetMapping()
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name ="size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name ="size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));

    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name ="size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, connectedUser));

    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name ="size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, connectedUser));

    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.updateShareableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, connectedUser));
    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {

        return ResponseEntity.ok(service.borrowBook(bookId, connectedUser));
    }

    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowedBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.returnBorrowedBook(bookId, connectedUser));
    }

    // methode pour que le propriétaire du livre approuve son retour apres avoir check son état
    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowedBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, connectedUser));
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ) {
        service.uploadBookCoverPicture(file, connectedUser, bookId);
        return ResponseEntity.accepted().build();
    }

}

/*
 * findAllBooks ====>  Récupère une page de livres disponibles, en fonction des paramètres de pagination fournis.
 *
 * @param page Le numéro de la page à récupérer. Ce paramètre est facultatif et a une valeur par défaut de 0.
 *             Il est extrait des paramètres de requête de l'URL.
 * @param size Le nombre de livres à récupérer par page. Ce paramètre est facultatif et a une valeur par défaut de 10.
 *             Il est extrait des paramètres de requête de l'URL.
 * @param connectedUser Un objet Authentication représentant l'utilisateur actuellement connecté.
 *                      Il est automatiquement injecté par le framework Spring Security.
 * @return Un ResponseEntity contenant un objet PageResponse<BookResponse> avec la page des livres demandée,
 *         et un statut HTTP 200 OK.

 * Fonctionnement détaillé :
 * 1. La méthode est annotée avec @GetMapping(), ce qui indique qu'elle gère les requêtes HTTP GET envoyées
 *    à l'URI "/books".
 * 2. Les paramètres @RequestParam(name = "page", defaultValue = "0", required = false) int page et
 *    @RequestParam(name = "size", defaultValue = "10", required = false) int size indiquent que les paramètres
 *    de pagination "page" et "size" sont optionnels dans la requête. Si non spécifiés, "page" sera 0 et "size" sera 10.
 * 3. Le paramètre Authentication connectedUser est injecté automatiquement par Spring Security et contient
 *    les détails de l'utilisateur actuellement connecté.
 * 4. La méthode appelle la méthode findAllBooks du service BookService, en passant les paramètres de pagination
 *    et l'objet Authentication. Le service traite la requête en récupérant la page de livres demandée.
 * 5. La méthode retourne un ResponseEntity contenant l'objet PageResponse<BookResponse> avec la page des livres
 *    demandée et un statut HTTP 200 OK.
 */