package com.dentalclinic.controllers;

import com.dentalclinic.entities.Salary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SalaryController {
    private EntityManager em;

    public SalaryController(EntityManager em){
        this.em = em;
    }

    public List<Salary> getAllSalaryRecord() {
        TypedQuery<Salary> query = em.createQuery(
                "SELECT s FROM Salary s", Salary.class);
        return query.getResultList();
    }

}
