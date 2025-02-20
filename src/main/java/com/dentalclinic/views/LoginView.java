package com.dentalclinic.views;

import com.dentalclinic.DentalClinic;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.entities.RoleType;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.entities.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginView {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    
    @FXML private Label errorLabel;
    private boolean hasErrors;

    @FXML
    private void initialize() {
        DentalClinic.setTitle("Hello! - DentalClinic");
        emailField.textProperty().addListener((prop, from, to) -> clearError());
        passwordField.textProperty().addListener((prop, from, to) -> clearError());
        clearError();
    }

    @FXML
    private void handleLogIn() {
        clearError();
        if (hasErrors) return;

        String email = emailField.getText();
        if (email == null || email.isEmpty()) {
            showError("Email is required");
            return;
        }

        String password = passwordField.getText();
        if (password == null || password.isEmpty()) {
            showError("Password is required");
            return;
        }
        Staff user = DatabaseController.user(email, password);
        if (user != null) {
            UserSession.setCurrentUser(user);

            System.out.println("Login successful! User ID: " + user.getStaffId() + ", Role: " + user.getRole());

            try {
                DentalClinic.loadStage("views/primary.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showError("Email or password is incorrect");
            System.out.println("Login failed for email: " + email);
        }
    }

    private void showError(String error) {
        errorLabel.setText(hasErrors ? errorLabel.getText() + "\n" + error : error);
        errorLabel.setVisible(true);
        hasErrors = true;
    }

    private void clearError() {
        errorLabel.setVisible(false);
        hasErrors = false;
    }
}
