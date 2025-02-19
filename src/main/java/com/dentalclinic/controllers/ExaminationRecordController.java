package com.dentalclinic.controllers;

import com.dentalclinic.entities.ExaminationRecord;
import com.dentalclinic.entities.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ExaminationRecordController {
    private EntityManager em;

    public ExaminationRecordController(EntityManager entityManager) {
        this.em = entityManager;
    }

    public List<ExaminationRecord> getAllExaminationList(){
        String jpql = "SELECT i FROM ExaminationRecord i";
        return em.createQuery(jpql, ExaminationRecord.class).getResultList();
    }

    public void saveRecord(ExaminationRecord record) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(record);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void updateExamination(ExaminationRecord examinationRecord) {
        if (examinationRecord == null) return;
        em.getTransaction().begin();
        try {
            em.merge(examinationRecord);  // Cập nhật dữ liệu vào DB
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();  // Hoàn tác nếu có lỗi
            e.printStackTrace();
        }
    }
}

