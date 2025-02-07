package com.dentalclinic.controllers;

import com.dentalclinic.entities.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public final class DatabaseController {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void init() {
        if (emf != null) return;
        emf = Persistence.createEntityManagerFactory("dental-clinic-database");
        em = emf.createEntityManager();
    }

    public static EntityManager getEntityManager() {
        return em;
    }

}
