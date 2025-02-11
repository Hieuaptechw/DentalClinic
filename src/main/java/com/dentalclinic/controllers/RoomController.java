package com.dentalclinic.controllers;

import com.dentalclinic.entities.Room;
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

    public void deleteRoom(Room room) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Cập nhật các cuộc hẹn để không còn tham chiếu đến phòng này
            String hql = "UPDATE Appointment a SET a.room = null WHERE a.room.id = :roomId";
            em.createQuery(hql).setParameter("roomId", room.getRoomId()).executeUpdate();

            // Sau khi cập nhật các cuộc hẹn, tiến hành xóa phòng
            Room roomToDelete = em.find(Room.class, room.getRoomId());
            if (roomToDelete != null) {
                em.remove(roomToDelete);
            }

            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }
}
