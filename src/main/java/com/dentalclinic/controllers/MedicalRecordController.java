package com.dentalclinic.controllers;

import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.entities.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
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

    public List<MedicalRecord> getMedicalRecordsByPatientId(Long patientId) {
        try {
            TypedQuery<MedicalRecord> query = em.createQuery(
                    "SELECT m FROM MedicalRecord m WHERE m.patient.id = :patientId", MedicalRecord.class);
            query.setParameter("patientId", patientId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Patient findPatientByName(String name) {
        try {
            String jpql = "SELECT p FROM Patient p WHERE p.name = :name";
            return em.createQuery(jpql, Patient.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void handleAddMedicalRecord(MedicalRecord medicalRecord) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(medicalRecord);
            transaction.commit();
            System.out.println("Medical record added successfully");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
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
}
