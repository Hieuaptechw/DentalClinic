package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.controllers.StaffController;
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

    @FXML private Label titleLabel;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<RoleType> roleBox;
    @FXML private TextField specialtyField;
    @FXML private Button submitButton;


    private Staff currentStaff = new Staff();

    @FXML
    private void initialize() {
        roleBox.getItems().setAll(RoleType.values());
        roleBox.setPlaceholder(new Label("Select role..."));
    }

    public void setStaff(Staff staff) {
        titleLabel.setText("Edit Staff");
        submitButton.setText("Update");

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

    @FXML
    private void handleSubmit() {
        try {
            currentStaff.setName(nameField.getText());
            currentStaff.setEmail(emailField.getText());
            currentStaff.setPhone(phoneField.getText());
            currentStaff.setRole(roleBox.getValue());
            currentStaff.setSpecialty(specialtyField.getText());
    
            String password = passwordField.getText();
            if (password != null && !password.isEmpty()) {
                currentStaff.setPassword(password);
            }
    
            StaffController controller = new StaffController(DatabaseController.getEntityManager());
            boolean isNew = idField.getText() == null || idField.getText().isEmpty();
            if (isNew) controller.addStaff(currentStaff);
            else controller.updateStaff(currentStaff);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, (isNew ? "Successfully added staff" : "Successfully updated staff"), ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
