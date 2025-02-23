package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.controllers.StaffController;
import com.dentalclinic.entities.Gender;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.RoleType;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.validation.StaffValidator;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StaffFormController {

    @FXML private Label titleLabel;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<RoleType> roleBox;
    @FXML private TextField specialtyField;
    @FXML private Button submitButton;
    @FXML private StaffValidator staffValidator;

private StaffController staffController;
    private Staff currentStaff = new Staff();

    @FXML
    private void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        staffController = new StaffController(em);
        staffValidator = new StaffValidator();
        roleBox.getItems().setAll(RoleType.values());
        roleBox.setPlaceholder(new Label("Select role..."));
    }

    public void setStaff(Staff staff) {
        titleLabel.setText("Edit Staff");
        submitButton.setText("Update");
        passwordField.setPromptText("Set to change password");
        nameField.setText(staff.getName());
        emailField.setText(staff.getEmail());
        phoneField.setText(staff.getPhone());
        specialtyField.setText(staff.getSpecialty());
        roleBox.setValue(staff.getRole());

        currentStaff = staff;
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        roleBox.setValue(null);
        specialtyField.clear();
    }

    @FXML
    private void handleSubmit() {
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                phoneField.getText().isEmpty() || roleBox.getValue() == null) {
            staffValidator.showAlert("Error", "All fields except specialty are required!");
            return;
        }
        if (!staffValidator.isValidName(nameField.getText())) {
            staffValidator.showAlert("Error", "Name can only contain letters!");
            return;
        }
        if (!staffValidator.isValidEmail(emailField.getText())) {
            staffValidator.showAlert("Error", "Invalid email format!");
            return;
        }

        if (!staffValidator.isValidPhone(phoneField.getText())) {
            staffValidator.showAlert("Error", "Invalid phone number!");
            return;
        }

        if (currentStaff.getStaffId() == 0 && staffController.isEmailExists(emailField.getText())) {
            staffValidator.showAlert("Error", "Email already exists!");
            return;
        }

        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.isEmpty() && !staffValidator.isValidPassword(password, confirmPassword)) {
            return;
        }

        if (!password.isEmpty()) {
            String hashedPassword = DatabaseController.hashPassword(password);
            currentStaff.setPassword(hashedPassword);
        }

        currentStaff.setName(nameField.getText());
        currentStaff.setEmail(emailField.getText());
        currentStaff.setPhone(phoneField.getText());
        currentStaff.setSpecialty(specialtyField.getText());
        currentStaff.setRole(roleBox.getValue());

        if (currentStaff.getStaffId() == 0) {
            currentStaff.setCreatedAt(LocalDateTime.now());
            currentStaff.setUpdatedAt(LocalDateTime.now());
            staffController.addStaff(currentStaff);
            staffValidator.showAlert("Success", "Staff added successfully!");
        } else {
            currentStaff.setUpdatedAt(LocalDateTime.now());
            staffController.updateStaff(currentStaff);
            staffValidator.showAlert("Success", "Staff updated successfully!");
        }

        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }



}
