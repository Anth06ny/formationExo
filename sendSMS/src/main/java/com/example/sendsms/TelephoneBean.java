package com.example.sendsms;

import java.io.Serializable;

/**
 * Created by Utilisateur on 08/06/2018.
 */

public class TelephoneBean implements Serializable {

    private String numero;
    private boolean send;

    public TelephoneBean(String numero) {
        this.numero = numero;
    }

    public TelephoneBean() {
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
