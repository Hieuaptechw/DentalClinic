package com.dentalclinic.controllers;

import com.dentalclinic.entities.Branch;
import com.dentalclinic.entities.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class BranchController {
    private EntityManager em;

    public BranchController(EntityManager em) {
        this.em = em;
    }

    public List<Branch> getAllBranches() {
        TypedQuery<Branch> query = em.createQuery("SELECT b FROM Branch b", Branch.class);
        return query.getResultList();
    }

    public void addBranch(Branch branch) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(branch);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deleteBranch(Long branchId) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Branch branch = em.find(Branch.class, branchId);
            if (branch != null) {
                em.remove(branch);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
    public void updateBranch(Branch branch) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(branch);
            transaction.commit();
            System.out.println("Cập nhật branch thành công!");
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
    }
}
