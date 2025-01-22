package com.dentalclinic.entity;

public class Category {
    private String code;
    private String name;
    private Status status;

    public Category(String code, String name, Status status) {
        this.code = code;
        this.name = name;
        this.status = status;
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


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }



    @Override
    public String toString() {
        return "Category{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
