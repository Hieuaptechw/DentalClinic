package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.Gender;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.RoleType;
import com.dentalclinic.entities.Staff;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StaffFormController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<RoleType> roleBox;
    @FXML private TextField specialtyField;

    private Staff currentStaff = new Staff();

    @FXML
    private void initialize() {
        roleBox.getItems().setAll(RoleType.values());
        roleBox.setPlaceholder(new Label("Select role..."));
    }

    public void setStaff(Staff staff) {

        passwordField.setPromptText("Set to change password");

        idField.setText(Long.toString(staff.getStaffId()));
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
}
