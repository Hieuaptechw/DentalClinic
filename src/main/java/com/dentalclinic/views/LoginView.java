package com.dentalclinic.views;

import com.dentalclinic.DentalClinic;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.entities.UserSession;
import com.dentalclinic.validation.LocalValidator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.List;

public class LoginView {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Label errorLabel;

    private LocalValidator loginValidator = new LocalValidator();

    @FXML
    private void initialize() {
        System.out.println(DatabaseController.hashPassword("123456"));
        DentalClinic.setTitle("Hello! - DentalClinic");
        emailField.textProperty().addListener((prop, from, to) -> clearError());
        passwordField.textProperty().addListener((prop, from, to) -> clearError());
        clearError();
    }

    @FXML
    private void handleLogIn() {
        clearError();

        String email = emailField.getText();
        String password = passwordField.getText();

        List<String> errors = loginValidator.validateInput(email, password);

        if (!errors.isEmpty()) {
            showError(String.join("\n", errors));
            return;
        }


        Staff user = DatabaseController.user(email, password);
        if (user != null) {
            UserSession.setCurrentUser(user);
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Login Successful");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Welcome, " + user.getName() + "!\nRole: " + user.getRole());
            successAlert.showAndWait();
            loadPrimary();
        } else {
            showError("Email or password is incorrect");
            System.out.println("Login failed for email: " + email);
        }
    }

    private void loadPrimary(){
        try{
            DentalClinic.loadStage("views/primary.fxml");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void showError(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setVisible(false);
    }
}
