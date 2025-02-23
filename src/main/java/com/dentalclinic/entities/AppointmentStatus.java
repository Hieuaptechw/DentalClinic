package com.dentalclinic.entities;

public enum AppointmentStatus {
    PENDING("Pending Confirmation"),
    ARRIVED("Patient Arrived"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    NO_SHOW("No Show");

    private final String label;

    AppointmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
