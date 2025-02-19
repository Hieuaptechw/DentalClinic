package com.dentalclinic.views.pages.form;

import com.dentalclinic.entities.Appointment;
import com.dentalclinic.entities.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;


public class AppointmentFormController {
    @FXML
    private TextField nameField, addressField, identityField,phoneField;
    @FXML
    private DatePicker datePicker,birthPicker;

    public void setAppointment(Patient patient){
        nameField.setText(patient.getName());
        addressField.setText(patient.getAddress());
        identityField.setText(patient.getAddress());
        phoneField.setText(patient.getPhone());
        birthPicker.setValue(patient.getDob());
       // datePicker.setValue(patient.getDob());
    }
}
