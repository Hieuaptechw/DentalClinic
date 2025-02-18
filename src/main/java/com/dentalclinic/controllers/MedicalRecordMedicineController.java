package com.dentalclinic.controllers;

import com.dentalclinic.entities.MedicalRecordMedicine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecordMedicineController {
    private EntityManager em;

    public MedicalRecordMedicineController(EntityManager em){
        this.em = em;
    }
    public List<MedicalRecordMedicine> getMedicinesByRecordId(long recordId) {
        try {
            TypedQuery<MedicalRecordMedicine> query = em.createQuery(
                    "SELECT mm FROM MedicalRecordMedicine mm WHERE mm.medicalRecord.recordId = :recordId", MedicalRecordMedicine.class);
            query.setParameter("recordId", recordId);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
