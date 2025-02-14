package com.dentalclinic.entities;

public enum RoleType {
    ADMIN("Administrator"),
    DOCTOR("Doctor"),
    RECEPTIONIST("Receptionist"),
    NURSE("Nurse"),
    PERSONNEL("Personnel");

    private final String labelRole;

    RoleType(String labelRole) {
        this.labelRole = labelRole;
    }

    public String getLabelRole() {
        return labelRole;
    }
}
