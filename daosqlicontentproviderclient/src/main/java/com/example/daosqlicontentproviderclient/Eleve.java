package com.example.daosqlicontentproviderclient;

import android.os.Parcel;
import android.os.Parcelable;

public class Eleve implements Parcelable {

    private long id;
    private String nom;
    private String prenom;
    private boolean sexe;

    public Eleve(long id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(final String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(final String prenom) {
        this.prenom = prenom;
    }

    public boolean isSexe() {
        return sexe;
    }

    public void setSexe(final boolean sexe) {
        this.sexe = sexe;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    protected Eleve(final Parcel in) {
        nom = in.readString();
        prenom = in.readString();
        sexe = in.readByte() != 0x00;
        id = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(nom);
        dest.writeString(prenom);
        dest.writeByte((byte) (sexe ? 0x01 : 0x00));
        dest.writeLong(id);
    }

    public static final Creator<Eleve> CREATOR = new Creator<Eleve>() {
        @Override
        public Eleve createFromParcel(final Parcel in) {
            return new Eleve(in);
        }

        @Override
        public Eleve[] newArray(final int size) {
            return new Eleve[size];
        }
    };

    @Override
    public String toString() {
        return id + " " + nom + " " + prenom;
    }
}
