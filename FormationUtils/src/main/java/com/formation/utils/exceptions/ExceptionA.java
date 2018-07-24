package com.formation.utils.exceptions;

/**
 * Created by amonteiro on 17/09/2014.
 */
public class ExceptionA extends Exception {

    //-----------------------
    // Constructeur
    //------------------------

    public ExceptionA() {
    }

    public ExceptionA(String message) {
        super(message);
    }

    public ExceptionA(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionA(Throwable cause) {
        super(cause);
    }

    public ExceptionA(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
