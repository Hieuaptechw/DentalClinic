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
            Inventory existingInventory = em.createQuery(
                            "SELECT i FROM Inventory i WHERE i.itemName = :name AND i.supplier = :supplier AND i.unitPrice = :price", Inventory.class)
                    .setParameter("name", inventory.getItemName())
                    .setParameter("supplier", inventory.getSupplier())
                    .setParameter("price", inventory.getUnitPrice())// Kiểm tra cùng nhà cung cấp
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (existingInventory != null) {
                // Nếu sản phẩm đã tồn tại với cùng nhà cung cấp, cập nhật số lượng
                existingInventory.setQuantity(existingInventory.getQuantity() + inventory.getQuantity());
                em.merge(existingInventory);
            } else {
                // Nếu chưa có hoặc nhà cung cấp khác, thêm mới vào database
                em.persist(inventory);
            }
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
