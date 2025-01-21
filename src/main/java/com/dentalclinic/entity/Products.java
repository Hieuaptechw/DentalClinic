package com.dentalclinic.entity;

import java.time.LocalDate;

public class Products {
    private String code;
    private String productName;
    private Category category;
    private Double price;
    private int quantity;
    private LocalDate expiryDate;
    private String supplier;

    public Products(String code, String productName, Category category, Double price, int quantity, LocalDate expiryDate, String supplier) {
        this.code = code;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.supplier = supplier;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return "Products{" +
                "code='" + code + '\'' +
                ", productName='" + productName + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", quantity=" + quantity +
                ", expiryDate=" + expiryDate +
                ", supplier='" + supplier + '\'' +
                '}';
    }
}
