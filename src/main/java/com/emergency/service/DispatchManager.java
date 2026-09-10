package com.emergency.service;

import com.emergency.exception.InvalidRequestException;
import com.emergency.model.*;

import java.util.*;

public class DispatchManager {
    private final Map<String, Ambulance> ambulances = new HashMap<>();
    private final List<EmergencyRequest> history = new ArrayList<>();
    
    // Priority queue to handle higher critical requests first
    private final PriorityQueue<EmergencyRequest> waitingQueue = new PriorityQueue<>(
            Comparator.comparingInt((EmergencyRequest r) -> r.getPriority().getLevel())
    );

    public void registerAmbulance(Ambulance ambulance) {
        ambulances.put(ambulance.getAmbulanceId(), ambulance);
    }

    public void submitEmergencyRequest(EmergencyRequest request) throws InvalidRequestException {
        validateRequest(request);
        history.add(request);
        
        Optional<Ambulance> bestAmbulance = findBestAvailableAmbulance(request);
        
        if (bestAmbulance.isPresent()) {
            allocateAmbulance(request, bestAmbulance.get());
        } else {
            request.setStatus("QUEUED");
            waitingQueue.add(request);
        }
    }

    private void validateRequest(EmergencyRequest request) throws InvalidRequestException {
        if (request.getPatientId() == null || request.getPatientId().trim().isEmpty() ||
            request.getPickupLocation() == null || request.getPickupLocation().trim().isEmpty() ||
            request.getDestinationHospital() == null || request.getDestinationHospital().trim().isEmpty() ||
            request.getEstimatedDistance() <= 0) {
            throw new InvalidRequestException("Invalid emergency request data provided.");
        }
    }

    private Optional<Ambulance> findBestAvailableAmbulance(EmergencyRequest request) {
        return ambulances.values().stream()
                .filter(Ambulance::isAvailable)
                .filter(amb -> isTypeSuitable(amb.getType(), request.getPriority()))
                .findFirst(); // In real cases, sorting by short distance would take place here
    }

    private boolean isTypeSuitable(AmbulanceType ambType, EmergencyPriority priority) {
        if (priority == EmergencyPriority.CRITICAL) {
            return ambType == AmbulanceType.ICU || ambType == AmbulanceType.ADVANCED_LIFE_SUPPORT;
        }
        return true; 
    }

    private void allocateAmbulance(EmergencyRequest request, Ambulance ambulance) {
        ambulance.setState(AmbulanceState.DISPATCHED);
        request.setAssignedAmbulanceId(ambulance.getAmbulanceId());
        request.setStatus("DISPATCHED");
    }

    public void updateAmbulanceState(String ambulanceId, AmbulanceState newState) {
        Ambulance ambulance = ambulances.get(ambulanceId);
        if (ambulance != null) {
            ambulance.setState(newState);
            
            // If the ambulance returns to active availability, check queue for automatic re-allocation
            if (newState == AmbulanceState.AVAILABLE && !waitingQueue.isEmpty()) {
                EmergencyRequest nextRequest = waitingQueue.poll();
                if (isTypeSuitable(ambulance.getType(), nextRequest.getPriority())) {
                    allocateAmbulance(nextRequest, ambulance);
                } else {
                    // Put back to queue if not matching requirements
                    waitingQueue.add(nextRequest);
                }
            }
        }
    }

    public List<EmergencyRequest> getHistory() {
        return new ArrayList<>(history);
    }

    public PriorityQueue<EmergencyRequest> getWaitingQueue() {
        return waitingQueue;
    }
}
