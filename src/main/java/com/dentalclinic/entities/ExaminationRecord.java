package com.dentalclinic.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "examination_records")
public class ExaminationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long examinationId;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = true) // Có thể NULL nếu không có lịch trước
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;  // Bác sĩ thực hiện khám

    @Column(name = "date_of_visit", nullable = false)
    private LocalDateTime dateOfVisit;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "symptoms")
    private String symptoms;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "treatment")
    private String treatment;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExaminationStatus status = ExaminationStatus.ONGOING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ExaminationRecord() {
    }

    public ExaminationRecord(Patient patient, Staff staff, LocalDateTime dateOfVisit, String reason, ExaminationStatus status, String symptoms, Room room) {
        this.patient = patient;
        this.staff = staff;
        this.dateOfVisit = dateOfVisit;
        this.reason = reason;
        this.status = status;
        this.symptoms = symptoms;
        this.room = room;
    }

    public long getExaminationId() {
        return examinationId;
    }

    public void setExaminationId(long examinationId) {
        this.examinationId = examinationId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public LocalDateTime getDateOfVisit() {
        return dateOfVisit;
    }

    public void setDateOfVisit(LocalDateTime dateOfVisit) {
        this.dateOfVisit = dateOfVisit;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ExaminationStatus getStatus() {
        return status;
    }

    public void setStatus(ExaminationStatus status) {
        this.status = status;
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

    // Getters & Setters
}
