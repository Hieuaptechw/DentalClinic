package com.dentalclinic.controllers;

import com.dentalclinic.entities.Financial;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.Staff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class FinancialController {
    private EntityManager em;

    public FinancialController(EntityManager em) {
        this.em = em;
    }

    public List<Financial> getAllPatients() {
        TypedQuery<Financial> query = em.createQuery("SELECT f FROM Financial f", Financial.class);
        return query.getResultList();
    }

    public List<Patient> getAllPatient() {
        TypedQuery<Patient> query = em.createQuery("SELECT p FROM Patient p", Patient.class);
        return query.getResultList();
    }

    public List<Staff> getAllStaff() {
        TypedQuery<Staff> query = em.createQuery("SELECT s FROM Staff s", Staff.class);
        return query.getResultList();
    }
    public void addFinancial(Financial financial) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(financial);
            transaction.commit();
            System.out.println("Thêm financi thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
    public void deleteFinancial(Long financialId) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Financial financial = em.find(Financial.class, financialId);
            if (financial != null) {
                em.remove(financial);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
    public void updateFinancial(Financial financial) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(financial);
            transaction.commit();
            System.out.println("Cập nhật financial thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
    }
}
