package com.dentalclinic.entities;

public enum PatientStatus {
    UNDER_TREATMENT("Under Treatment"),
    DISCHARGED("Discharged");
    private final String labelStatus;
    PatientStatus(String labelStatus) {
        this.labelStatus = labelStatus;
    }
    public String getLabelStatus() {
        return labelStatus;
    }
}
