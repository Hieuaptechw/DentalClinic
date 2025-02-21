package com.dentalclinic.validation;

import com.dentalclinic.entities.Gender;
import com.dentalclinic.entities.PatientStatus;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class PatientValidator {
    public static String validatePatientData(String name, String email, String identity, String phone, String address, LocalDate dob, Gender gender, PatientStatus status) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty!";
        }
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Invalid email format!";
        }
        if (identity == null || identity.trim().isEmpty()) {
            return "Identity card cannot be empty!";
        }
        if (phone == null || phone.trim().isEmpty() || !phone.matches("^\\d{10,11}$")) {
            return "Invalid phone number!";
        }
        if (address == null || address.trim().isEmpty()) {
            return "Address cannot be empty!";
        }
        if (dob == null || dob.isAfter(LocalDate.now())) {
            return "Invalid date of birth!";
        }
        if (gender == null) {
            return "Please select gender!";
        }
        if (status == null) {
            return "Please select patient status!";
        }
        return null;
    }
}
