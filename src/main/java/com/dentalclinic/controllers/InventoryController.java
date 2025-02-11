package com.dentalclinic.controllers;

import com.dentalclinic.entities.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;


import java.util.List;

public class InventoryController {
    private EntityManager em;

    public InventoryController(EntityManager em){
        this.em = em;
    }

    public List<Inventory> getAllInventory(){
        String jpql = "SELECT i FROM Inventory i";
        return em.createQuery(jpql, Inventory.class).getResultList();
    }

    public void addInventory(Inventory inventory){
        EntityTransaction transaction = em.getTransaction();
        try{
            transaction.begin();
            em.persist(inventory);
            transaction.commit();
        }catch(Exception e){
            if(transaction.isActive()){
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
    }
    
    public void handleDeleteInventory(Long inventoryId){
        em.getTransaction().begin();
        try {
            Inventory inventory = em.find(Inventory.class, inventoryId);
            if (inventory != null) {
                em.remove(inventory);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public void updateInventory(Inventory inventory) {
        if (inventory == null) return;

        em.getTransaction().begin();
        try {
            em.merge(inventory);  // Cập nhật dữ liệu vào DB
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();  // Hoàn tác nếu có lỗi
            e.printStackTrace();
        }
    }




}
