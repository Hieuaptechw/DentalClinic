package com.dentalclinic.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roomId;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Room() {
    }

    public Room(String roomNumber, Branch branch, String roomType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.roomNumber = roomNumber;
        this.branch = branch;
        this.roomType = roomType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Room(long roomId, String roomNb, Branch selectedBranch, String selectedRoomType, LocalDateTime createdAt, LocalDateTime now) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.branch = branch;
        this.roomType = roomType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return roomType;
    }

}
