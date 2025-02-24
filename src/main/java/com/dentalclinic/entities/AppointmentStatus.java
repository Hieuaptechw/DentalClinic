package com.dentalclinic.entities;

public enum AppointmentStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String label;

    AppointmentStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
