package com.dentalclinic.entity;

public enum Status {
    AVAILABLE("Có sẵn"),
    OUT_OF_STOCK("Hết hàng"),
    EXPIRED("Hết hạn");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
