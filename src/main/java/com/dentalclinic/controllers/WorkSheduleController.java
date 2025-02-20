package com.dentalclinic.controllers;


import com.dentalclinic.entities.Room;
import com.dentalclinic.entities.WorkSchedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class WorkSheduleController {
    private EntityManager em;

    public WorkSheduleController(EntityManager em) {
        this.em = em;
    }

    public List<WorkSchedule> getAllWorkSchedules() {
        TypedQuery<WorkSchedule> query = em.createQuery("SELECT w FROM WorkSchedule w", WorkSchedule.class);
        return query.getResultList();
    }
    public List<WorkSchedule> getAllWorkSchedulesToday() {
        LocalDate today = LocalDate.now();
        TypedQuery<WorkSchedule> query = em.createQuery(
                "SELECT w FROM WorkSchedule w WHERE w.workingDay = :today",
                WorkSchedule.class
        );
        query.setParameter("today", today);
        return query.getResultList();
    }
    public void addWorkSchedule(WorkSchedule workSchedule) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(workSchedule);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }
    public void deleteWorkSchedule(Long workScheduleId) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            WorkSchedule workSchedule = em.find(WorkSchedule.class, workScheduleId);
            if (workSchedule != null) {
                em.remove(workSchedule);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
    public void updateWorkSchedule(WorkSchedule workSchedule) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(workSchedule);
            transaction.commit();
            System.out.println("Cập nhật lịch làm việc thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
    }

}
