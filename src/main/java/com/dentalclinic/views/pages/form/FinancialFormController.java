package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.FinancialController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.*;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FinancialFormController {
    @FXML private TextField invoiceNumberField;
    @FXML private TextField amountField;
    @FXML private TextArea descriptionField;
    @FXML private TextField emailStaffField;
    @FXML private TextField phonePatiendField;
    @FXML private DatePicker exportDateField ;
    @FXML private ComboBox<String> paymentMethodCombobox;
    @FXML private ComboBox<String> statusCombobox;
    @FXML private RadioButton radioIncome;
    @FXML private RadioButton radioExpenses;
    @FXML private ToggleGroup transactionTypeGroup;
    private FinancialController financialController;
    private Financial financial;
    private Staff staff;
    private List<Staff> staffListF;
    private List<Patient> patientListF;

    @FXML
    private void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        financialController = new FinancialController(em);
        transactionTypeGroup = new ToggleGroup();
        radioIncome.setToggleGroup(transactionTypeGroup);
        radioExpenses.setToggleGroup(transactionTypeGroup);
        fixComboBox(paymentMethodCombobox);
        fixComboBox(statusCombobox);
        setupComboBox();
        setupComboBoxListeners();
    }
    public void setFinancial(Financial financial,List<Staff> staffList,List<Patient> patientList) {
           staffListF = staffList;
           patientListF = patientList;
        String phonePatiend="Not Have!";
        this.financial = financial;
        System.out.println(financial);
        invoiceNumberField.setText(financial.getInvoiceNumber());
        amountField.setText(String.valueOf(financial.getAmount()));
        exportDateField.setValue(financial.getCreatedAt().toLocalDate());
        descriptionField.setText(financial.getDescription());
        if (financial.getTransactionType() == TransactionType.INCOME) {
            radioIncome.setSelected(true);
            phonePatiend = financial.getPatient().getPhone();
        } else {
            radioExpenses.setSelected(true);
        }
        emailStaffField.setText(financial.getUpdatedBy().getEmail());
        phonePatiendField.setText(phonePatiend);
        paymentMethodCombobox.setValue(financial.getPaymentMethod());
        statusCombobox.setValue(String.valueOf(financial.getStatus()));
        staff =financial.getUpdatedBy();
        System.out.println(patientListF.size());
        System.out.println(staffListF.size());

    }
    public void setupComboBox(){
        List<String> paymentMethods = List.of("Cash", "Credit Card", "Debit Card", "Insurance", "Bank Transfer");
        List<String> status = List.of("Pending", "Completed", "Cancelled", "Refunded");
        statusCombobox.setItems(FXCollections.observableArrayList(status));
        paymentMethodCombobox.setItems(FXCollections.observableArrayList(paymentMethods));
    }
    private void setupComboBoxListeners() {
        paymentMethodCombobox.setOnAction(event -> {
            String paymentMethod = paymentMethodCombobox.getSelectionModel().getSelectedItem();
            if (paymentMethod != null) {
                System.out.println("Payment method được chọn: " +paymentMethod);
            }
        });
        statusCombobox.setOnAction(event -> {
            String status = statusCombobox.getSelectionModel().getSelectedItem();
            if (status != null) {
                System.out.println("Status được chọn :" +status);
            }
        });

    }

    private <T> void fixComboBox(ComboBox<T> cb) {
        cb.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty) ;
                if (empty || item == null) {
                    setText(cb.getPromptText());
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        amountField.clear();
        descriptionField.clear();
        emailStaffField.clear();
        phonePatiendField.clear();
        exportDateField.setValue(null);
        paymentMethodCombobox.setValue(null);
        statusCombobox.setValue(null);
        statusCombobox.setPromptText("Status :");
        paymentMethodCombobox.setPromptText("Payment Method :");
        transactionTypeGroup.selectToggle(null);

    }
    @FXML
    private void handleUpdate(){
        String invoiceNumber = invoiceNumberField.getText();
        System.out.println(invoiceNumber);
        Double amount = Double.parseDouble(amountField.getText());
        String description = descriptionField.getText();
        String emailStaff = emailStaffField.getText();
        String phonePatient = phonePatiendField.getText();
        String paymentMethod = paymentMethodCombobox.getValue();

        String status = statusCombobox.getValue();
        TransactionType transactionType = null;
        FinancialStatus financialStatus = null;
        switch (status){
            case "Pending":
                financialStatus= (FinancialStatus.PENDING);
                break;
            case "Completed":
                financialStatus=(FinancialStatus.COMPLETED);
                break;
            case "Cancelled":
                financialStatus =(FinancialStatus.CANCELLED);
                break;
            case "Refunded":
                financialStatus= (FinancialStatus.REFUNDED);
                break;
            default:
                System.out.println("Error");

        }
        if (radioExpenses.isSelected()) {
            transactionType = TransactionType.EXPENSE;
        } else if (radioIncome.isSelected()) {
            transactionType = TransactionType.INCOME;
        }
        if (transactionType == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a transaction type!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        Optional<Patient> selectedPatient = Optional.empty();
        if (transactionType == TransactionType.INCOME) {
            selectedPatient = patientListF.stream()
                    .filter(p -> p.getPhone().equals(phonePatient))
                    .findFirst();

            if (selectedPatient.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Patient not found with phone: " + phonePatient, ButtonType.OK);
                alert.showAndWait();
                return;
            }
        }
        System.out.println(financialStatus);
        Patient patient = selectedPatient.orElse(null);
        LocalDateTime now = LocalDateTime.now();
        Financial financialupdate = new Financial(patient,amount,description,now,now,transactionType,paymentMethod,invoiceNumber,financialStatus,staff);
        System.out.println(financialupdate);
        financialController.updateFinancial(financialupdate);
    }
}
