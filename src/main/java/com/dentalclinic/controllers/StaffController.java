package com.dentalclinic.controllers;

import com.dentalclinic.entities.RoleType;
import com.dentalclinic.entities.Staff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class StaffController {
    private EntityManager em;

    public StaffController(EntityManager em) {
        this.em = em;
    }

    public List<Staff> getAllStaff() {
        TypedQuery<Staff> query = em.createQuery("SELECT s FROM Staff s", Staff.class);
        return query.getResultList();
    }

    public List<Staff> getDoctors() {
        TypedQuery<Staff> query = em.createQuery(
                "SELECT s FROM Staff s WHERE s.role = :role", Staff.class);
        RoleType role = RoleType.valueOf("DOCTOR");
        query.setParameter("role", role); // Giả sử role của bác sĩ là "Doctor"
        return query.getResultList();
    }

    public Staff getStaffById(Long staffId) {
        return em.find(Staff.class, staffId);
    }


    public void addStaff(Staff staff) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(staff);
            transaction.commit();
            System.out.println("Thêm nhân viên thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    public void updateStaff(Staff staff) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(staff);
            transaction.commit();
            System.out.println("Cập nhật nhân viên thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
    }

    public void deleteStaff(Long staffId) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Staff staff = em.find(Staff.class, staffId);
            if (staff != null) {
                em.remove(staff);
            }
            transaction.commit();
            System.out.println("Xóa nhân viên thành công!");
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
}