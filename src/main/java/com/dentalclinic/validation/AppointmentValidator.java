package com.dentalclinic.validation;

import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AppointmentValidator {
    public static boolean isValidReason(String reason) {
        return reason != null && !reason.trim().isEmpty() && reason.length() >= 5;
    }
    public static boolean isValidDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    public static boolean isValidSymptoms(String symptoms) {
        return symptoms != null && !symptoms.trim().isEmpty() && symptoms.length() >= 5;
    }
    public static boolean isValidTime(String time) {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
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
