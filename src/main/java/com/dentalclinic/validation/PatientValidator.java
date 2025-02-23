package com.dentalclinic.validation;

import com.dentalclinic.entities.Gender;
import com.dentalclinic.entities.PatientStatus;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class PatientValidator {
    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    public static boolean isValidName(String name) {
        return name.matches("^[A-Za-zÀ-Ỹà-ỹ\\s]+$");
    }
    public static boolean isValidIndentityCard(String indentityCard) {
        return indentityCard.matches("^\\d{12}$");
    }
    public static boolean isValidPhone(String phone) {
        return phone.matches("^\\d{10,11}$");
    }
    public static boolean isValidAddress(String address) {
        return address.matches("^[\\p{L}0-9,.\\-/# ]{5,100}$");
    }
    public static boolean isValidBirthDay(LocalDate birthDay) {
        if (birthDay == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        if (birthDay.isAfter(today)) {
            return false;
        }
        int age = Period.between(birthDay, today).getYears();
        return age > 5;
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
