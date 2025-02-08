package com.dentalclinic.controllers;

import com.dentalclinic.entities.Appointment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CandidateNumberController {
    private EntityManager em;

    public CandidateNumberController(EntityManager em) {
        this.em = em;
    }

    public List<Appointment> getAllCandidateNumbers() {
        TypedQuery<Appointment> query = em.createQuery("SELECT a FROM Appointment a", Appointment.class);
        return query.getResultList();
    }

}
