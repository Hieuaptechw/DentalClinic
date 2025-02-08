package com.dentalclinic.views;

import com.dentalclinic.DentalClinic;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.administration.DoctorPage;
import com.dentalclinic.views.pages.administration.SalaryPage;
import com.dentalclinic.views.pages.administration.WorkSchedulePage;
import com.dentalclinic.views.pages.manage.*;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.setting.BackupPage;
import com.dentalclinic.views.pages.setting.NotificationPage;
import com.dentalclinic.views.pages.setting.SupportPage;
import com.dentalclinic.views.pages.setting.SystemPage;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;

import java.io.IO;
import java.io.IOException;
import java.net.URL;

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
        if (hasErrors) return;

        String email = emailField.getText();
        if (email == null || email.isEmpty()) {
            showError("Email is required");
        }
        String password = passwordField.getText();
        if (password == null || password.isEmpty()) {
            showError("Password is required");
        }
        if (hasErrors) return;

        boolean success = DatabaseController.logIn(email, password);
        if (success) {
            try { DentalClinic.loadStage("views/primary.fxml"); }
            catch (IOException e) {}
        } else {
            showError("Email or password is incorrect");
            return;
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
