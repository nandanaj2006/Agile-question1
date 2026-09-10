package com.emergency.model;

public enum EmergencyPriority {
    CRITICAL(1), 
    HIGH(2), 
    MODERATE(3), 
    NORMAL(4);

    private final int level;

    EmergencyPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
