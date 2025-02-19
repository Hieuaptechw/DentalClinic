package com.dentalclinic.controllers;

import com.dentalclinic.entities.Appointment;
import com.dentalclinic.entities.Branch;
import com.dentalclinic.entities.Room;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.entities.RoleType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class AppointmentController {
    private EntityManager em;

    public AppointmentController(EntityManager em) {
        this.em = em;
    }

    public List<Appointment> getAppointments() {
        TypedQuery<Appointment> query = em.createQuery("SELECT a FROM Appointment a", Appointment.class);
        return query.getResultList();
    }

    public List<Staff> getStaffs() {
        TypedQuery<Staff> query = em.createQuery("SELECT s FROM Staff s", Staff.class);
        return query.getResultList();
    }

    public List<Room> getRooms() {
        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
        return query.getResultList();
    }

    public List<Branch> getBranches() {
        TypedQuery<Branch> query = em.createQuery("SELECT b FROM Branch b", Branch.class);
        return query.getResultList();
    }


    public Staff getDoctorWithFewestAppointments() {
        TypedQuery<Staff> query = em.createQuery(
                "SELECT s FROM Staff s WHERE s.role = :role ORDER BY " +
                        "(SELECT COUNT(a) FROM Appointment a WHERE a.staff = s) ASC",
                Staff.class);
        query.setParameter("role", RoleType.DOCTOR);
        query.setMaxResults(1);
        List<Staff> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }


    public Room findAvailableRoom(String roomType, LocalDate date) {
        TypedQuery<Room> query = em.createQuery(
                "SELECT r FROM Room r WHERE r.roomType = :roomType " +
                        "AND NOT EXISTS (SELECT a FROM Appointment a WHERE a.room = r AND DATE(a.appointmentDate) = :date)",
                Room.class);
        query.setParameter("roomType", roomType);
        query.setParameter("date", date);
        query.setMaxResults(1);

        List<Room> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
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
    public void deleteAppointment(Long appointmentId) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Appointment appointment = em.find(Appointment.class, appointmentId);
            if (appointment != null) {
                em.remove(appointment);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

}
