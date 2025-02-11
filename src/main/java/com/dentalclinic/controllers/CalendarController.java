package com.dentalclinic.controllers;

import com.dentalclinic.entities.Appointment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CalendarController {
    private EntityManager em;

    public CalendarController(EntityManager em){
        this.em = em;
    }

    public List<Appointment> getAppointments(){
        TypedQuery<Appointment> query = em.createQuery("SELECT a FROM Appointment a", Appointment.class);
        return query.getResultList();
    }

    
}
