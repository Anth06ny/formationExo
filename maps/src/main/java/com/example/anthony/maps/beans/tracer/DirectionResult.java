package com.example.anthony.maps.beans.tracer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

/**
 * Created by Anthony on 11/01/2017.
 */
public class DirectionResult {
    private ArrayList<Route> routes;
    private String status;

    //Calculer
    private ArrayList<LatLng> lstLatLng = null;
    private LatLngBounds latLngBounds = null;
    private LatLng startPosition = null, stopPosition = null;

    public DirectionResult() {
    }

    public DirectionResult(ArrayList<Route> routes, String status) {
        this.routes = routes;
        this.status = status;
    }

    /* ---------------------------------
    //
    // -------------------------------- */

    public LatLngBounds getLatLngBounds() {
        if (lstLatLng == null) {
            fillPolyLine();
        }
        return latLngBounds;
    }

    public ArrayList<LatLng> getLstLatLng() {
        if (lstLatLng == null) {
            fillPolyLine();
        }
        return lstLatLng;
    }

    public LatLng getStartPosition() {
        if (lstLatLng == null) {
            fillPolyLine();
        }
        return startPosition;
    }

    public LatLng getStopPosition() {
        if (lstLatLng == null) {
            fillPolyLine();
        }
        return stopPosition;
    }

    /* ---------------------------------
    // Private
    // -------------------------------- */

    /**
     * @return liste de Latlng correspondant au trajet
     */
    private void fillPolyLine() {
        lstLatLng = new ArrayList<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (routes != null) {
            for (Route route : routes) {
                if (route.getLegs() != null) {
                    for (Leg leg : route.getLegs()) {
                        if (leg.getSteps() != null) {
                            for (Step step : leg.getSteps()) {
                                lstLatLng.add(step.getStart_location().getLatLng());
                                builder.include(step.getStart_location().getLatLng());
                            }
                        }
                        else if (leg.getStart_location() != null) {
                            lstLatLng.add(leg.getStart_location().getLatLng());
                        }
                        if (leg.getEnd_location() != null) {
                            lstLatLng.add(leg.getEnd_location().getLatLng());
                        }
                    }
                }
            }
        }

        if (!lstLatLng.isEmpty()) {
            startPosition = lstLatLng.get(0);
            stopPosition = lstLatLng.get(lstLatLng.size() - 1);
        }

        latLngBounds = builder.build();
    }

    /* ---------------------------------
    //  Getter /Setter
    // -------------------------------- */

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
