package com.dentalclinic.entity;

public class Category {
    private String code;
    private String name;


    public Category(String code, String name) {
        this.code = code;
        this.name = name;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }





    @Override
    public String toString() {
        return "Category{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
