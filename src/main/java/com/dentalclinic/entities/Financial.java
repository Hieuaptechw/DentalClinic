package com.dentalclinic.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_management")
public class Financial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long financeId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = true)
    private Patient patient;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FinancialStatus status;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Staff updatedBy;

    public Financial() {
    }

    public Financial(Patient patient, double amount, String description, LocalDateTime createdAt, LocalDateTime updatedAt, TransactionType transactionType, String paymentMethod, String invoiceNumber, FinancialStatus status, Staff updatedBy) {
        this.patient = patient;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.transactionType = transactionType;
        this.paymentMethod = paymentMethod;
        this.invoiceNumber = invoiceNumber;
        this.status = status;
        this.updatedBy = updatedBy;
    }

    public long getFinanceId() {
        return financeId;
    }

    public void setFinanceId(long financeId) {
        this.financeId = financeId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }


    public FinancialStatus getStatus() {
        return status;
    }

    public void setStatus(FinancialStatus status) {
        this.status = status;
    }

    public Staff getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Staff updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "FinancialManagement{" +
                "financeId=" + financeId +
                ", patient=" + patient +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", transactionType=" + transactionType +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", status=" + status +
                ", updatedBy=" + updatedBy +
                '}';
    }
}
