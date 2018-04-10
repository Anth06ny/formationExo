package com.example.anthony.maps.beans.metro;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anthony on 12/01/2018.
 */

public class StationMetroBean {

    private LatLng position;
    private String name;
    private int ligne;

    public StationMetroBean() {
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }
}
