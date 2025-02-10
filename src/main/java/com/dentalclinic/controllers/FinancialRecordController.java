package com.dentalclinic.controllers;

import com.dentalclinic.entities.FinancialRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class FinancialRecordController {
    private EntityManager em;

    public FinancialRecordController(EntityManager em) {
        this.em = em;
    }

    public List<FinancialRecord> getAllFinancialRecords() {
        TypedQuery<FinancialRecord> query = em.createQuery("SELECT fr FROM FinancialRecord fr", FinancialRecord.class);
        return query.getResultList();
    }

    public void addFinancialRecord(FinancialRecord financialRecord) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(financialRecord);
            transaction.commit();
            System.out.println("Thêm thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    public void updateFinancialRecord(FinancialRecord financialRecord) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(financialRecord);
            transaction.commit();
            System.out.println("Cập nhật thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
    }
    public void deleteFinancialRecord(Long financialRecordId) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            FinancialRecord financialRecord = em.find(FinancialRecord.class, financialRecordId);
            if (financialRecord != null) {
                em.remove(financialRecord);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
}
