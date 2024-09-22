package com.davidnhn.book.handler;


import com.davidnhn.book.exceptions.OperationNotPermittedException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.davidnhn.book.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;


@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestionnaire d'exception pour les comptes verrouillés.
     * @param exp L'exception LockException.
     * @return La réponse avec le code d'erreur et le statut HTTP.
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_LOCKED.getCode()) // de l'enum BusinessErrorCodes
                                .businessErrorDescription((ACCOUNT_LOCKED.getDescription()))
                                .error(exp.getMessage())
                                .build()
                );
    }

    /**
     * Gestionnaire d'exception pour les comptes désactivés.
     * @param exp L'exception DisabledException.
     * @return La réponse avec le code d'erreur et le statut HTTP.
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_DISABLED.getCode()) // de l'enum BusinessErrorCodes
                                .businessErrorDescription((ACCOUNT_DISABLED.getDescription()))
                                .error(exp.getMessage())
                                .build()
                );
    }

    /**
     * Gestionnaire d'exception pour les identifiants incorrects.
     * @param exp L'exception BadCredentialsException.
     * @return La réponse avec le code d'erreur et le statut HTTP.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BAD_CREDENTIALS.getCode()) // de l'enum BusinessErrorCodes
                                .businessErrorDescription((BAD_CREDENTIALS.getDescription()))
                                .error(exp.getMessage())
                                .build()
                );
    }

    /**
     * Gestionnaire d'exception pour les erreurs de messagerie.
     * @param exp L'exception MessagingException.
     * @return La réponse avec le statut HTTP.
     */
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    // pour les erreur de validation comme Joi mais en java
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        // log the exception
        exp.printStackTrace();
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal error code, contact admin ")
                                .error(exp.getMessage())
                                .build()
                );
    }


    /**
     * L'émetteur de la requête n'est pas autorisé à aller au bout de cette opération
     * @param exp L'exception OperationNotPermittedException.
     * @return La réponse avec le statut HTTP.
     */
    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }
}


/*
 * @RestControllerAdvice est une annotation de Spring utilisée pour gérer les exceptions au niveau global dans une application Spring Boot.
 * Elle combine les fonctionnalités de @ControllerAdvice et @ResponseBody.

 * Fonctionnement et caractéristiques :

 * 1. Global Exception Handling :
 *    - @RestControllerAdvice permet de définir des gestionnaires d'exceptions globales pour tous les contrôleurs REST.
 *    - Cela signifie que vous pouvez centraliser la gestion des exceptions et éviter la duplication du code de gestion des erreurs dans chaque contrôleur.

 * 2. Combinaison de @ControllerAdvice et @ResponseBody :
 *    - @ControllerAdvice est utilisée pour définir un composant global pour gérer les exceptions et les conseils (advice) de contrôleurs.
 *    - @ResponseBody garantit que les réponses des méthodes de gestion des exceptions sont sérialisées au format JSON ou XML.

 * 3. Utilisation des méthodes @ExceptionHandler :
 *    - Les méthodes annotées avec @ExceptionHandler à l'intérieur de @RestControllerAdvice interceptent les exceptions spécifiées.
 *    - Ces méthodes peuvent renvoyer des objets ResponseEntity personnalisés contenant des informations sur l'erreur, comme un message d'erreur, un code de statut HTTP, etc.

 * 4. Spécification des Exceptions :
 *    - Vous pouvez spécifier des exceptions spécifiques que chaque méthode @ExceptionHandler doit gérer en passant le type d'exception en paramètre.
 *    - Exemple : @ExceptionHandler(LockedException.class) gère toutes les LockedException.

 * 5. Avantages :
 *    - Centralisation : Simplifie la maintenance et améliore la lisibilité du code en centralisant la gestion des exceptions.
 *    - Réutilisation : Permet de réutiliser le même code de gestion des erreurs pour différents contrôleurs.
 *    - Flexibilité : Vous pouvez définir différents gestionnaires pour différents types d'exceptions et personnaliser les réponses.

 * Exemple d'utilisation :
 *
 * @RestControllerAdvice
 * public class GlobalExceptionHandler {
 *
 *     @ExceptionHandler(LockedException.class)
 *     public ResponseEntity<ExceptionResponse> handleLockedException(LockedException exp) {
 *         return ResponseEntity
 *                 .status(HttpStatus.UNAUTHORIZED)
 *                 .body(new ExceptionResponse("Account is locked", exp.getMessage()));
 *     }
 *
 *     @ExceptionHandler(Exception.class)
 *     public ResponseEntity<ExceptionResponse> handleGenericException(Exception exp) {
 *         return ResponseEntity
 *                 .status(HttpStatus.INTERNAL_SERVER_ERROR)
 *                 .body(new ExceptionResponse("Internal server error", exp.getMessage()));
 *     }
 * }

 * Dans cet exemple :
 * - handleLockedException gère les exceptions LockedException et renvoie une réponse avec un statut UNAUTHORIZED.
 * - handleGenericException gère toutes les autres exceptions et renvoie une réponse avec un statut INTERNAL_SERVER_ERROR.
 *
 * @RestControllerAdvice est donc un outil puissant pour améliorer la gestion des exceptions dans les applications Spring Boot,
 * en fournissant une approche centralisée et flexible.
 */
