package com.example.exemple.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.
/**
 * Entity mapped to table ELEVE.
 */
public class Eleve {

    private Long id;
    private String Nom;
    private String Prenom;

    public Eleve() {
    }

    public Eleve(Long id) {
        this.id = id;
    }

    public Eleve(Long id, String Nom, String Prenom) {
        this.id = id;
        this.Nom = Nom;
        this.Prenom = Prenom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String Nom) {
        this.Nom = Nom;
    }

    public String getPrenom() {
        return Prenom;
    }

    public void setPrenom(String Prenom) {
        this.Prenom = Prenom;
    }

}
