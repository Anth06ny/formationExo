package com.formation.utils.exceptions;

/**
 * Exception du à une erreur technique du code, ne doit pas se produire sinon c'est notre faute.
 * Serveur qui ne répond plus
 */
public class TechnicalException extends ExceptionA {

    /**
     * On affichera un message par defaut pour l'utilisateur
     *
     * @param messageTechnique
     */
    public TechnicalException(String messageTechnique) {
        this("Une erreur technique est intervenue", null);
    }

    /**
     * On affichera un message par defaut pour l'utilisateur
     *
     * @param messageTechnique
     */
    public TechnicalException(String messageTechnique, Throwable throwable) {
        super( "Une erreur technique est intervenue", messageTechnique, throwable);
    }

}
