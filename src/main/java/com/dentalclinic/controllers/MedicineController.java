package com.dentalclinic.controllers;

import com.dentalclinic.entities.Medicine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MedicineController {
    private EntityManager em;

    public MedicineController(EntityManager em){
        this.em = em;
    }

    public List<Medicine> getAllMedicine() {
        TypedQuery<Medicine> query = em.createQuery("SELECT m FROM Medicine m", Medicine.class);
        return query.getResultList();
    }
    public Medicine getMedicineByName(String name) {
        try {
            TypedQuery<Medicine> query = em.createQuery("SELECT m FROM Medicine m WHERE m.name = :name", Medicine.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


}
