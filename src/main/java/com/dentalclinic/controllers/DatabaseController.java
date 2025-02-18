package com.dentalclinic.controllers;

import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.RoleType;
import com.dentalclinic.entities.Staff;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

public final class DatabaseController {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    private static Staff currentUser = null;
    
    public static Staff getCurrentUser() {
        return currentUser;
    }

    public static void init() {
        if (emf != null) return;
        emf = Persistence.createEntityManagerFactory("dental-clinic-database");
        em = emf.createEntityManager();
    }

    public static EntityManager getEntityManager() {
        return em;
    }

    public static RoleType logIn(String email, String password) {
        TypedQuery<Staff> query = em.createQuery(
                "SELECT s FROM Staff s WHERE s.email=:email AND s.password=:password",
                Staff.class);
        query.setParameter("email", email);
        query.setParameter("password", password);

        Staff s = query.getResultList().stream().findAny().orElse(null);
        if (s != null) {
            currentUser = s;
            return s.getRole(); // Trả về role của user
        }
        return null;
    }

}
