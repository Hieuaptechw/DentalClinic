package com.dentalclinic.controllers;

import com.dentalclinic.entities.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import javax.swing.text.html.parser.Entity;
import java.util.List;

public class InventoryController {
    private EntityManager em;

    public InventoryController(EntityManager em){
        this.em = em;
    }

    public List<Inventory> getAllInventory(){
        TypedQuery<Inventory> query = em.createQuery("SELECT i FROM Inventory i", Inventory.class);
        return query.getResultList();
    }

    
}
