package com.emergency.service;

import com.emergency.exception.InvalidRequestException;
import com.emergency.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DispatchManagerTest {
    private DispatchManager dispatchManager;

    @BeforeEach
    void setUp() {
        dispatchManager = new DispatchManager();
    }

    @Test
    void testSuccessfulAmbulanceAllocation() throws InvalidRequestException {
        Ambulance ambulance = new Ambulance("AMB01", AmbulanceType.ICU, "John Doe");
        dispatchManager.registerAmbulance(ambulance);

        EmergencyRequest request = new EmergencyRequest("P101", EmergencyPriority.CRITICAL, "Point A", "Hospital B", 12.5);
        dispatchManager.submitEmergencyRequest(request);

        assertEquals("DISPATCHED", request.getStatus());
        assertEquals("AMB01", request.getAssignedAmbulanceId());
        assertEquals(AmbulanceState.DISPATCHED, ambulance.getState());
    }

    @Test
    void testWaitingQueueAndAutoAllocation() throws InvalidRequestException {
        // Submit request without registering any ambulance first
        EmergencyRequest request = new EmergencyRequest("P102", EmergencyPriority.HIGH, "Point X", "Hospital Y", 5.0);
        dispatchManager.submitEmergencyRequest(request);

        assertEquals("QUEUED", request.getStatus());
        assertEquals(1, dispatchManager.getWaitingQueue().size());

        // Registering available ambulance triggers automatic processing
        Ambulance ambulance = new Ambulance("AMB02", AmbulanceType.ADVANCED_LIFE_SUPPORT, "Jane Doe");
        dispatchManager.registerAmbulance(ambulance);
        
        // Simulating resource update loop check
        dispatchManager.updateAmbulanceState("AMB02", AmbulanceState.AVAILABLE);

        assertEquals("DISPATCHED", request.getStatus());
        assertEquals("AMB02", request.getAssignedAmbulanceId());
    }

    @Test
    void testInvalidRequestException() {
        EmergencyRequest badRequest = new EmergencyRequest("", EmergencyPriority.NORMAL, "Point A", "", -5.0);
        assertThrows(InvalidRequestException.class, () -> dispatchManager.submitEmergencyRequest(badRequest));
    }
}
