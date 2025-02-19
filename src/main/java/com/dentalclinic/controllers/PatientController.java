package com.dentalclinic.controllers;

import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.PatientStatus;
import com.dentalclinic.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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

    public Patient getPatientById(Long patientId) {
        return em.find(Patient.class, patientId);
    }

    public void addPatient(Patient patient) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(patient);
            transaction.commit();
            System.out.println("Thêm bệnh nhân thành công!");
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
            System.out.println("Cập nhật bệnh nhân thành công!");
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







}
