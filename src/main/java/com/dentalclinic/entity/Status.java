package com.dentalclinic.entity;

public enum Status {
    ACTIVE,
    INACTIVE;


    public static Status fromString(String status) {
        switch (status.toUpperCase()) {
            case "ACTIVE":
                return ACTIVE;
            case "INACTIVE":
                return INACTIVE;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
