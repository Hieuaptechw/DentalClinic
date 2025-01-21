package com.dentalclinic.entity;

public enum Category {
    DENTAL("Nha khoa"),
    MEDICAL_EQUIPMENT("Thiết bị y tế"),
    DENTAL_MATERIALS("Vật liệu nha khoa"),
    DENTAL_TOOLS("Dụng cụ nha khoa"),
    ORAL_HYGIENE("Vệ sinh răng miệng"),
    SUPPLEMENTARY_PRODUCTS("Chế phẩm bổ sung"),
    OTHER("Khác");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
