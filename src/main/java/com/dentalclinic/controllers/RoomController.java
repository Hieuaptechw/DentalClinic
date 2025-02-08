package com.dentalclinic.controllers;

import com.dentalclinic.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RoomController {
    private EntityManager em;
    public RoomController(EntityManager em) {
        this.em = em;
    }

    public List<Room> getAllRooms() {
        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
        return query.getResultList();
    }
}
