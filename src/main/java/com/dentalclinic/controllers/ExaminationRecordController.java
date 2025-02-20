package com.dentalclinic.controllers;

import com.dentalclinic.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public List<ExaminationRecord> getAllExaminationListOfDoctor(Long staffId) {
        String jpql = "SELECT i FROM ExaminationRecord i WHERE i.staff.id = :staffId";
        return em.createQuery(jpql, ExaminationRecord.class)
                .setParameter("staffId", staffId)
                .getResultList();
    }

    public Room findAvailableRoom(String roomType, LocalDateTime dateTime) {
        TypedQuery<Room> query = em.createQuery(
                "SELECT r FROM Room r WHERE r.roomType = :roomType " +
                        "AND NOT EXISTS (SELECT a FROM ExaminationRecord a WHERE a.room = r " +
                        "AND a.dateOfVisit = :dateTime)", // So sánh trực tiếp LocalDateTime
                Room.class);

        query.setParameter("roomType", roomType);
        query.setParameter("dateTime", dateTime);
        query.setMaxResults(1);

        List<Room> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Staff getDoctorWithFewestAppointments() {
        TypedQuery<Staff> query = em.createQuery(
                "SELECT s FROM Staff s WHERE s.role = :role ORDER BY " +
                        "(SELECT COUNT(a) FROM ExaminationRecord a WHERE a.staff = s) ASC",
                Staff.class);
        query.setParameter("role", RoleType.DOCTOR);
        query.setMaxResults(1);
        List<Staff> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public void createExaminationRecord(ExaminationRecord record) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(record);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public void updateExamination(ExaminationRecord examinationRecord) {
        if (examinationRecord == null) return;
        em.getTransaction().begin();
        try {
            em.merge(examinationRecord);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}

