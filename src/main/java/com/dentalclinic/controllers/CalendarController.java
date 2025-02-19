package com.dentalclinic.controllers;

import com.dentalclinic.entities.Appointment;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.Room;
import com.dentalclinic.entities.Staff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
    public List<Room> getRooms(){
        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
        return query.getResultList();
    }
    public List<Staff> getStaffs(){
        TypedQuery<Staff> query = em.createQuery("SELECT s FROM Staff s", Staff.class);
        return query.getResultList();
    }
    public void addAppointment(Appointment appointment) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(appointment);
            transaction.commit();
            System.out.println("Thêm lịch hẹn thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
    public void updateAppointment(Appointment appointment) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(appointment);
            transaction.commit();
            System.out.println("Cập nhật appointment thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
    }

}
