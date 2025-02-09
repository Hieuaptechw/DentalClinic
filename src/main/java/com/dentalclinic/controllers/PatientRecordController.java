package com.dentalclinic.controllers;

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

}
