package com.dentalclinic.controllers;

import com.dentalclinic.entities.Branch;
import com.dentalclinic.entities.WorkSchedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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

}
