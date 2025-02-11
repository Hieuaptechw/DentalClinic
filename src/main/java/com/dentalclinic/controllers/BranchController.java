package com.dentalclinic.controllers;

import com.dentalclinic.entities.Branch;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class BranchController {
    private EntityManager em;

    public BranchController(EntityManager em) {
        this.em = em;
    }

    // Lấy tất cả các chi nhánh
    public List<Branch> getAllBranches() {
        TypedQuery<Branch> query = em.createQuery("SELECT b FROM Branch b", Branch.class);
        return query.getResultList();
    }

    // Thêm mới chi nhánh
    public void addBranch(Branch branch) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(branch);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e; // Ném ngoại lệ lại để bên ngoài có thể xử lý
        }
    }

    // Xóa chi nhánh
    public void deleteBranch(Branch branch) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Branch branchToDelete = em.find(Branch.class, branch.getBranchId());
            if (branchToDelete != null) {
                // Kiểm tra nếu chi nhánh có phòng khám, không xóa nếu đang có dữ liệu liên kết
                if (branchToDelete.getRooms() != null && !branchToDelete.getRooms().isEmpty()) {
                    throw new IllegalArgumentException("Cannot delete branch with associated rooms.");
                }
                em.remove(branchToDelete);  // Xóa chi nhánh
            }
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e; // Ném ngoại lệ lại để bên ngoài có thể xử lý
        }
    }

    // Cập nhật chi nhánh
    public void updateBranch(Branch branch) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Merge để cập nhật thông tin chi nhánh
            em.merge(branch);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e; // Ném ngoại lệ lại để bên ngoài có thể xử lý
        }
    }
}
