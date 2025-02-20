package com.dentalclinic.controllers;

import com.dentalclinic.entities.Financial;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.entities.Medicine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
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
            return query.getSingleResult();  // Trả về kết quả đầu tiên
        } catch (NoResultException e) {
            return null; // Nếu không tìm thấy thuốc, trả về null
        }
    }


}
