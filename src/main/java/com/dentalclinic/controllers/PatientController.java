package com.dentalclinic.controllers;

import com.dentalclinic.entities.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class PatientController {
    private EntityManager em;

    public PatientController(EntityManager em) {
        this.em = em;
    }

    public List<Patient> getAllPatients() {
        TypedQuery<Patient> query = em.createQuery("SELECT p FROM Patient p", Patient.class);
        return query.getResultList();
    }
}
