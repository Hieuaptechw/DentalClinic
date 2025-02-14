package com.dentalclinic.controllers;

import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.Salary;
import com.dentalclinic.entities.Staff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class SalaryController {
    private EntityManager em;

    public SalaryController(EntityManager em){
        this.em = em;
    }

    public List<Salary> getAllSalaryRecord() {
        TypedQuery<Salary> query = em.createQuery(
                "SELECT s FROM Salary s", Salary.class);
        return query.getResultList();
    }

    public List<Staff> getAllRole() {
        try {
            String jpql = "SELECT s FROM Staff s";
            return em.createQuery(jpql, Staff.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    public Staff findStaffByEmailAndName(String email, String name) {
        try {
            String jpql = "SELECT s FROM Staff s WHERE s.email = :email AND s.name = :name";
            return em.createQuery(jpql, Staff.class)
                    .setParameter("email", email)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Salary findSalaryByStaff(Staff staff) {
        try {
            String jpql = "SELECT s FROM Salary s WHERE s.staff = :staff";
            return em.createQuery(jpql, Salary.class)
                    .setParameter("staff", staff)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    public void handleAddSalary(Salary salary) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(salary);
            transaction.commit();
            System.out.println("Thêm lương thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    public void updateSalary(Salary salary) {
        try {
            em.getTransaction().begin();
            em.merge(salary);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

}
