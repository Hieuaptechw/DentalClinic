package com.dentalclinic.entities;

public enum ExaminationStatus {
    ONGOING("In Progress"),
    NO_SHOW("No Show"),
    COMPLETED("Completed");

    private final String label;

    ExaminationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
