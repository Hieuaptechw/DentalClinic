package com.dentalclinic.entity;

import java.time.LocalDate;

public class Products {
    private String code;
    private String productName;
    private String categoryName;
    private Double price;
    private int quantity;
    private LocalDate expiryDate;
    private String supplier;
    private Double totalPrice;
    private Status status;

    public Products(String code, String productName, String categoryName, Double price, int quantity, LocalDate expiryDate, String supplier, double totalPrice, Status status) {
        this.code = code;
        this.productName = productName;
        this.categoryName = categoryName;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;

        this.supplier = supplier;
        this.totalPrice = totalPrice;
        this.status = status;
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


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Products{" +
                "code='" + code + '\'' +
                ", productName='" + productName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", expiryDate=" + expiryDate +
                ", supplier='" + supplier + '\'' +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                '}';
    }
}
