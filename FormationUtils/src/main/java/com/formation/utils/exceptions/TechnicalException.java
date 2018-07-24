package com.formation.utils.exceptions;

/**
 * Exception du à une erreur technique du code, ne doit pas se produire sinon c'est notre faute.
 * Serveur qui ne répond plus
 */
public class TechnicalException extends ExceptionA {

    public TechnicalException() {
    }

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TechnicalException(Throwable cause) {
        super(cause);
    }

    public TechnicalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
