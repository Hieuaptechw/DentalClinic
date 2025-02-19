package com.dentalclinic.controllers;

import com.dentalclinic.entities.Branch;
import com.dentalclinic.entities.RoleType;
import com.dentalclinic.entities.Room;
import com.dentalclinic.entities.Staff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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

    public Room getRoomById(Long roomId) {
        return em.find(Room.class, roomId);
    }

    public void addRoom(Room room) {
        em.getTransaction().begin();
        em.persist(room);
        em.getTransaction().commit();
    }

    public void updateRoom(Room room) {
        em.getTransaction().begin();
        em.merge(room);
        em.getTransaction().commit();
    }

    public void saveRoom(Room room) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            if (room.getRoomId() == 0) {
                em.persist(room);  // Nếu là phòng mới, thêm mới
            } else {
                em.merge(room);  // Nếu là phòng đã có, cập nhật
            }
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deleteRoom(Long roomId) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Room room = em.find(Room.class, roomId);
            if (room != null) {
                em.remove(room);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
}
