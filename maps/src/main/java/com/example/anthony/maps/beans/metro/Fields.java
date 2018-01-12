
package com.example.anthony.maps.beans.metro;

import java.util.List;

public class Fields {

    private Geo_shape geo_shape;
    private String nom;
    private String ligne;
    private String etat;
    private List<Double> geo_point_2d = null;

    public Geo_shape getGeo_shape() {
        return geo_shape;
    }

    public void setGeo_shape(Geo_shape geo_shape) {
        this.geo_shape = geo_shape;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLigne() {
        return ligne;
    }

    public void setLigne(String ligne) {
        this.ligne = ligne;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public List<Double> getGeo_point_2d() {
        return geo_point_2d;
    }

    public void setGeo_point_2d(List<Double> geo_point_2d) {
        this.geo_point_2d = geo_point_2d;
    }

}
