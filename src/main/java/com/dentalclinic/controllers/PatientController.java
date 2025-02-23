package com.dentalclinic.controllers;

import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.PatientStatus;
import com.dentalclinic.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
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
    public List<Patient> getLatestPatients() {
        TypedQuery<Patient> query = em.createQuery(
                "SELECT p FROM Patient p ORDER BY p.createdAt DESC", Patient.class);
        query.setMaxResults(5);
        return query.getResultList();
    }

    public long countPatients() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(p) FROM Patient p", Long.class);
        return query.getSingleResult();
    }
    public List<Object[]> getPatientsPerMonth() {
        String sql = "SELECT MONTH(p.createdAt) as month, COUNT(p.id) as count " +
                "FROM Patient p GROUP BY MONTH(p.createdAt) ORDER BY MONTH(p.createdAt)";
        Query query = em.createQuery(sql);
        return query.getResultList();
    }
    public Patient getPatientById(Long patientId) {
        return em.find(Patient.class, patientId);
    }

    public void addPatient(Patient patient) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(patient);
            transaction.commit();
            System.out.println("Patient added successfully!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    public void updatePatient(Patient patient) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(patient);
            transaction.commit();
            System.out.println("Patient updated successfully!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
    }


    public void deletePatient(Long patientId) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Patient patient = em.find(Patient.class, patientId);
            if (patient != null) {
                em.remove(patient);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
    public Patient findPatientByIdentityCard(String identityCard) {
        try {
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT p FROM Patient p WHERE p.identityCard = :identityCard", Patient.class);
            query.setParameter("identityCard", identityCard);

            List<Patient> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean checkIdentityCardExists(String identityCard) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM Patient p WHERE p.identityCard = :identityCard", Long.class
            );
            query.setParameter("identityCard", identityCard);

            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





}
