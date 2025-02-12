package com.dentalclinic.controllers;

import com.dentalclinic.entities.MedicalRecord;
import jakarta.persistence.EntityManager;

import java.util.List;

public class MedicalRecordController {
    private EntityManager em;

    public MedicalRecordController(EntityManager em){
        this.em = em;
    }

    public List<MedicalRecord> getAllPatientRecord(){
        String jpql = "SELECT i FROM MedicalRecord i";
        return em.createQuery(jpql, MedicalRecord.class).getResultList();
    }

    public void handleDeletePatientRecord(Long recordId){
        em.getTransaction().begin();
        try {
            MedicalRecord medicalRecord = em.find(MedicalRecord.class, recordId);
            if (medicalRecord != null) {
                em.remove(medicalRecord);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public void updatePatientRecord(MedicalRecord medicalRecord) {
        if (medicalRecord == null) return;
        em.getTransaction().begin();
        try {
            em.merge(medicalRecord);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

}
