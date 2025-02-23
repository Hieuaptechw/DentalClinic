package com.dentalclinic.validation;

import javafx.scene.control.Alert;

public class StaffValidator {
    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    public static boolean isValidName(String name) {
        return name.matches("^[A-Za-zÀ-Ỹà-ỹ\\s]+$");
    }

    public static boolean isValidPhone(String phone) {
        return phone.matches("^\\d{10,11}$");
    }
    public static boolean isValidPassword(String password, String confirmPassword) {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Password fields cannot be empty!");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match!");
            return false;
        }
        if (password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long!");
            return false;
        }
        return true;
    }
    public static void showAlert(String title, String message) {
        Alert alert;
        if (title.toLowerCase().equals("error")) {
             alert = new Alert(Alert.AlertType.ERROR);
        }else {
             alert = new Alert(Alert.AlertType.INFORMATION);
        }


        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
