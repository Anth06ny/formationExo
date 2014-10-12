package com.example.fragmentfromscratch.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Eleve implements Parcelable {

    private String nom;
    private String prenom;

    public Eleve(final String nom, final String prenom) {
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

    protected Eleve(final Parcel in) {
        nom = in.readString();
        prenom = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(nom);
        dest.writeString(prenom);
    }

    public static final Parcelable.Creator<Eleve> CREATOR = new Parcelable.Creator<Eleve>() {
        @Override
        public Eleve createFromParcel(final Parcel in) {
            return new Eleve(in);
        }

        @Override
        public Eleve[] newArray(final int size) {
            return new Eleve[size];
        }
    };
}