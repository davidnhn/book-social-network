package com.davidnhn.book.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum BusinessErrorCodes {

    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(302, FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(404, BAD_REQUEST, "Login and / or password is incorrect"),
    ;

    private final int code;
    private final String description;
    private final HttpStatus httpStatus;


    BusinessErrorCodes(int code,HttpStatus httpStatus,String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}


/* les 300 sont utilisé pour les redirections et peuvent preter a confusiion
    // Code 0 réservé pour les cas non implémentés ou inconnus
    // Utilisation de NOT_IMPLEMENTED (501) pour indiquer une fonctionnalité non supportée
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),

    // Utilisation de codes à partir de 10000 pour éviter toute confusion avec les codes HTTP standard
    // BAD_REQUEST (400) utilisé pour les erreurs liées aux entrées utilisateur incorrectes
    INCORRECT_CURRENT_PASSWORD(10001, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(10002, BAD_REQUEST, "The new password does not match"),

    // FORBIDDEN (403) utilisé pour les problèmes d'accès liés au compte
    ACCOUNT_LOCKED(10003, FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(10004, FORBIDDEN, "User account is disabled"),

    // Pour les identifiants incorrects, on pourrait utiliser UNAUTHORIZED (401)
    // au lieu de BAD_REQUEST pour mieux refléter la nature de l'erreur
    BAD_CREDENTIALS(10005, UNAUTHORIZED, "Login and / or password is incorrect"),

 */