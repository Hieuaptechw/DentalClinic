package com.dentalclinic.controllers;

import com.dentalclinic.entities.Inventory;
import com.dentalclinic.entities.MedicalRecord;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PatientRecordController {
    private EntityManager em;

    public PatientRecordController(EntityManager em){
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
            em.merge(medicalRecord);  // Cập nhật dữ liệu vào DB
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();  // Hoàn tác nếu có lỗi
            e.printStackTrace();
        }
    }

}
