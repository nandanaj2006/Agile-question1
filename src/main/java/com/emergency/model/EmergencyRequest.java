package com.emergency.model;

public class EmergencyRequest {
    private final String patientId;
    private final EmergencyPriority priority;
    private final String pickupLocation;
    private final String destinationHospital;
    private final double estimatedDistance;
    private String assignedAmbulanceId;
    private String status;

    public EmergencyRequest(String patientId, EmergencyPriority priority, String pickupLocation, 
                            String destinationHospital, double estimatedDistance) {
        this.patientId = patientId;
        this.priority = priority;
        this.pickupLocation = pickupLocation;
        this.destinationHospital = destinationHospital;
        this.estimatedDistance = estimatedDistance;
        this.status = "PENDING";
    }

    public String getPatientId() { return patientId; }
    public EmergencyPriority getPriority() { return priority; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDestinationHospital() { return destinationHospital; }
    public double getEstimatedDistance() { return estimatedDistance; }
    public String getAssignedAmbulanceId() { return assignedAmbulanceId; }
    public String getStatus() { return status; }

    public void setAssignedAmbulanceId(String assignedAmbulanceId) {
        this.assignedAmbulanceId = assignedAmbulanceId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double calculateETA() {
        // Average speed assumed: 50 km/h. ETA in minutes = (distance / 50) * 60
        return (estimatedDistance / 50.0) * 60.0;
    }
}
