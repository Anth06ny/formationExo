package com.example.anthony.maps.beans;

import android.graphics.Color;

import com.example.anthony.maps.beans.tracer.DirectionResult;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Anthony on 04/05/2017.
 */

public class Trajet {

    private MarkerOptions depart, arrivee;
    private PolylineOptions polylineOptions;
    private LatLngBounds latLngBounds;

    public Trajet(DirectionResult directionResult) throws Exception {

        if (directionResult == null || directionResult.getLstLatLng() == null || directionResult.getLstLatLng().isEmpty()) {
            throw new Exception("Chemin incorrect");
        }

        polylineOptions = new PolylineOptions();
        polylineOptions.addAll(directionResult.getLstLatLng());
        polylineOptions.color(Color.BLUE);

        //On déclare un marker vert que l'on placera sur le départ
        depart = new MarkerOptions();
        depart.position(directionResult.getStartPosition());
        depart.title("Début");
        depart.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        //On déclare un marker rouge que l'on mettra sur l'arrivée
        arrivee = new MarkerOptions();
        arrivee.position(directionResult.getStopPosition());
        arrivee.title("Fin");
        arrivee.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        //LE cadre
        latLngBounds = directionResult.getLatLngBounds();
    }

    public MarkerOptions getDepart() {
        return depart;
    }

    public void setDepart(MarkerOptions depart) {
        this.depart = depart;
    }

    public MarkerOptions getArrivee() {
        return arrivee;
    }

    public void setArrivee(MarkerOptions arrivee) {
        this.arrivee = arrivee;
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }
}
