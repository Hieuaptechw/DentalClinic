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
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

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
    private Room room; // Thay đổi từ String sang Room

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ExaminationRecord() {
    }

    public ExaminationRecord(Patient patient, Staff staff,LocalDateTime createdAt, LocalDateTime dateOfVisit, String reason, String symptoms, Room room) {
        this.patient = patient;
        this.staff = staff;
        this.createdAt = createdAt;
        this.dateOfVisit = dateOfVisit;
        this.reason = reason;
        this.symptoms = symptoms;
        this.room = room;
    }

    public ExaminationRecord(Patient patient, Staff staff, LocalDateTime dateOfVisit, String reason, String symptoms, String diagnosis, String treatment, Room room, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.patient = patient;
        this.staff = staff;
        this.dateOfVisit = dateOfVisit;
        this.reason = reason;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.room = room;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getExaminationId() {
        return examinationId;
    }

    public void setExaminationId(long examinationId) {
        this.examinationId = examinationId;
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
        return "ExaminationRecord{" +
                "examinationId=" + examinationId +
                ", patient=" + patient +
                ", staff=" + staff +
                ", dateOfVisit=" + dateOfVisit +
                ", reason='" + reason + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatment='" + treatment + '\'' +
                ", room=" + room +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
