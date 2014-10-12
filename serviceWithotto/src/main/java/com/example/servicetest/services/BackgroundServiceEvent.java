package com.example.servicetest.services;

public class BackgroundServiceEvent {

    private final double latitude, longitude;

    public BackgroundServiceEvent(final double latitude, final double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
