package com.example.servicetest.services;

public class StopBackgroundServiceEvent {
    boolean stopService;
    boolean restartService;

    public StopBackgroundServiceEvent(final boolean stopService, final boolean restartService) {
        super();
        this.stopService = stopService;
        this.restartService = restartService;
    }
}
