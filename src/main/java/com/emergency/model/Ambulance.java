package com.emergency.model;

public class Ambulance {
    private final String ambulanceId;
    private final AmbulanceType type;
    private AmbulanceState state;
    private final String driverDetails;

    public Ambulance(String ambulanceId, AmbulanceType type, String driverDetails) {
        this.ambulanceId = ambulanceId;
        this.type = type;
        this.driverDetails = driverDetails;
        this.state = AmbulanceState.AVAILABLE;
    }

    public String getAmbulanceId() { return ambulanceId; }
    public AmbulanceType getType() { return type; }
    public AmbulanceState getState() { return state; }
    public String getDriverDetails() { return driverDetails; }

    public boolean isAvailable() {
        return this.state == AmbulanceState.AVAILABLE;
    }

    public void setState(AmbulanceState state) {
        this.state = state;
    }
}
