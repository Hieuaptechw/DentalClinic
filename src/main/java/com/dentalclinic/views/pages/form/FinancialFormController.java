package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.FinancialController;
import com.dentalclinic.entities.*;
import com.dentalclinic.validation.LocalValidator;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class FinancialFormController {
    @FXML private TextField invoiceNumberField;
    @FXML private TextField amountField;
    @FXML private TextArea descriptionField;
    @FXML private TextField emailStaffField;
    @FXML private TextField phonePatiendField;
    @FXML private DatePicker exportDateField;
    @FXML private ComboBox<String> paymentMethodCombobox;
    @FXML private ComboBox<String> statusCombobox;
    @FXML private RadioButton radioIncome;
    @FXML private RadioButton radioExpenses;
    @FXML private ToggleGroup transactionTypeGroup;

    private FinancialController financialController;
    private Financial financial;
    private List<Staff> staffListF;
    private List<Patient> patientListF;
    private final LocalValidator validator = new LocalValidator();

    @FXML
    private void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        financialController = new FinancialController(em);
        transactionTypeGroup = new ToggleGroup();
        radioIncome.setToggleGroup(transactionTypeGroup);
        radioExpenses.setToggleGroup(transactionTypeGroup);
        setupComboBox();
    }

    public void setFinancial(Financial financial, List<Staff> staffList, List<Patient> patientList) {
        staffListF = staffList;
        patientListF = patientList;
        this.financial = financial;

        invoiceNumberField.setText(financial.getInvoiceNumber());
        amountField.setText(String.valueOf(financial.getAmount()));
        exportDateField.setValue(financial.getCreatedAt().toLocalDate());
        descriptionField.setText(financial.getDescription());
        emailStaffField.setText(financial.getUpdatedBy().getEmail());
        phonePatiendField.setText(financial.getPatient() != null ? financial.getPatient().getPhone() : "N/A");
        paymentMethodCombobox.setValue(financial.getPaymentMethod());
        statusCombobox.setValue(String.valueOf(financial.getStatus()));

        if (financial.getTransactionType() == TransactionType.INCOME) {
            radioIncome.setSelected(true);
        } else {
            radioExpenses.setSelected(true);
        }
    }

    private void setupComboBox() {
        List<String> paymentMethods = List.of("Cash", "Credit Card", "Debit Card", "Insurance", "Bank Transfer");
        List<String> statuses = List.of("Pending", "Completed", "Cancelled", "Refunded");
        paymentMethodCombobox.setItems(FXCollections.observableArrayList(paymentMethods));
        statusCombobox.setItems(FXCollections.observableArrayList(statuses));
    }

    @FXML
    private void handleUpdate() {
        try {
            String invoiceNumber = invoiceNumberField.getText().trim();
            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();
            String emailStaff = emailStaffField.getText().trim();
            String phonePatient = phonePatiendField.getText().trim();
            String paymentMethod = paymentMethodCombobox.getValue();
            String status = statusCombobox.getValue();

            // Validation
            if (invoiceNumber.isEmpty()) {
                showError("Invoice Number không được để trống!");
                return;
            }
            if (amountText.isEmpty()) {
                showError("Amount không được để trống!");
                return;
            }
            if (!amountText.matches("\\d+(\\.\\d{1,2})?")) {
                showError("Amount phải là số hợp lệ!");
                return;
            }
            if (!validator.emailValid(emailStaff)) {
                showError("Email của nhân viên không hợp lệ!");
                return;
            }
            if (paymentMethod == null) {
                showError("Vui lòng chọn phương thức thanh toán!");
                return;
            }
            if (status == null) {
                showError("Vui lòng chọn trạng thái!");
                return;
            }
            if (transactionTypeGroup.getSelectedToggle() == null) {
                showError("Vui lòng chọn loại giao dịch (Income/Expense)!");
                return;
            }

            FinancialStatus financialStatus = FinancialStatus.valueOf(status.toUpperCase());
            TransactionType transactionType = radioIncome.isSelected() ? TransactionType.INCOME : TransactionType.EXPENSE;
            Optional<Patient> selectedPatient = patientListF.stream().filter(p -> p.getPhone().equals(phonePatient)).findFirst();
            Optional<Staff> selectedStaff = staffListF.stream().filter(s -> s.getEmail().equals(emailStaff)).findFirst();

            if (transactionType == TransactionType.INCOME && selectedPatient.isEmpty()) {
                showError("Không tìm thấy bệnh nhân với số điện thoại: " + phonePatient);
                return;
            }
            if (selectedStaff.isEmpty()) {
                showError("Không tìm thấy nhân viên với email: " + emailStaff);
                return;
            }

            Double amount = Double.parseDouble(amountText);
            Financial financialUpdate = new Financial(selectedPatient.orElse(null), amount, description,
                    financial.getCreatedAt(), LocalDateTime.now(), transactionType,
                    paymentMethod, invoiceNumber, financialStatus, selectedStaff.get());

            if (financial != null) {
                financialUpdate.setFinanceId(financial.getFinanceId());
            }

            financialController.updateFinancial(financialUpdate);
            showSuccess("Cập nhật thành công!");
            closeForm();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Đã có lỗi xảy ra, vui lòng thử lại!");
        }
    }

    private void closeForm() {
        Stage stage = (Stage) invoiceNumberField.getScene().getWindow();
        stage.close();
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
