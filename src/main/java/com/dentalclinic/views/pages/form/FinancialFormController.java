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
import javafx.stage.Stage;

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
    private void handleUpdate() {
        try {
            String invoiceNumber = invoiceNumberField.getText().trim();
            System.out.println(invoiceNumber);
            if (invoiceNumber.isEmpty()) {
                showError("Invoice Number không được để trống!");
                return;
            }
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                showError("Amount không được để trống!");
                return;
            }
            Double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                showError("Amount phải là số hợp lệ!");
                return;
            }

            String description = descriptionField.getText().trim();
            String emailStaff = emailStaffField.getText().trim();
            String phonePatient = phonePatiendField.getText().trim();
            String paymentMethod = paymentMethodCombobox.getValue();
            if (paymentMethod == null) {
                showError("Vui lòng chọn phương thức thanh toán!");
                return;
            }

            String status = statusCombobox.getValue();
            if (status == null) {
                showError("Vui lòng chọn trạng thái!");
                return;
            }
            FinancialStatus financialStatus;
            switch (status.toLowerCase()) {
                case "pending":
                    financialStatus = FinancialStatus.PENDING;
                    break;
                case "completed":
                    financialStatus = FinancialStatus.COMPLETED;
                    break;
                case "cancelled":
                    financialStatus = FinancialStatus.CANCELLED;
                    break;
                case "refunded":
                    financialStatus = FinancialStatus.REFUNDED;
                    break;
                default:
                    showError("Trạng thái không hợp lệ!");
                    return;
            }
            TransactionType transactionType = null;
            if (radioExpenses.isSelected()) {
                transactionType = TransactionType.EXPENSE;
            } else if (radioIncome.isSelected()) {
                transactionType = TransactionType.INCOME;
            }
            if (transactionType == null) {
                showError("Vui lòng chọn loại giao dịch!");
                return;
            }
            Optional<Patient> selectedPatient = Optional.empty();
            if (transactionType == TransactionType.INCOME) {
                selectedPatient = patientListF.stream()
                        .filter(p -> p.getPhone().equals(phonePatient))
                        .findFirst();

                if (selectedPatient.isEmpty()) {
                    showError("Không tìm thấy bệnh nhân với số điện thoại: " + phonePatient);
                    return;
                }
            }
            Optional<Staff> selectedStaff = staffListF.stream()
                    .filter(staff -> staff.getEmail().equals(emailStaff))
                    .findFirst();
            if (selectedStaff.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Staff not found with email: " + emailStaff, ButtonType.OK);
                alert.showAndWait();
                System.out.println();
                return;
            }
            LocalDateTime createdAt =financial.getCreatedAt();
            Patient patient = selectedPatient.orElse(null);
            LocalDateTime now = LocalDateTime.now();
            Financial financialUpdate = new Financial(patient, amount, description, createdAt, now,
                    transactionType, paymentMethod, invoiceNumber, financialStatus, selectedStaff.get());

            System.out.println(financialUpdate);
            if (financial != null) {
                financialUpdate.setFinanceId(financial.getFinanceId());
            }
            financialController.updateFinancial(financialUpdate);
            Stage stage = (Stage) invoiceNumberField.getScene().getWindow();
            stage.close();
            showSuccess("Cập nhật thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Đã có lỗi xảy ra, vui lòng thử lại!");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

}
