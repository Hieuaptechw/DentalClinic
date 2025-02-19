package com.dentalclinic.controllers;

import com.dentalclinic.entities.ExaminationRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ExaminationRecordController {
    private EntityManager entityManager;

    public ExaminationRecordController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void saveRecord(ExaminationRecord record) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(record);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}

