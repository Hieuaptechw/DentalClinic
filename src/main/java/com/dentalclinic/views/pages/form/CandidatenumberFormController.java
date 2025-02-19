package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.CandidateNumberController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.Appointment;
import com.dentalclinic.entities.Gender;
import com.dentalclinic.entities.Patient;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.time.format.DateTimeFormatter;

public class CandidatenumberFormController {
    @FXML
    private TextField patientNameField;
    @FXML
    private TextField patientGenderField;
    @FXML
    private TextField patientEmailField;
    @FXML
    private TextField patientPhoneField;
    @FXML
    private TextArea patientAddressArea;
    @FXML
    private DatePicker patientDobPicker;
    @FXML
    private TextField queueNumberField;
    @FXML
    private TextField roomNameField;
    @FXML
    private TextField examTimeField;
    @FXML
    private TextField doctorExaminesField;
    @FXML
    private TextArea symptomPresentationField;
    private Appointment appointment;
    private CandidateNumberController candidateNumberController;
    @FXML
    public void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        candidateNumberController = new CandidateNumberController(em);

    }
    public void setInforPatient(Appointment appointment) {
        this.appointment = appointment;
        patientNameField.setText(appointment.getPatient().getName());
        patientGenderField.setText(appointment.getPatient().getGender().toString());
        patientPhoneField.setText(appointment.getPatient().getPhone());
        patientAddressArea.setText(appointment.getPatient().getAddress());
        patientEmailField.setText(appointment.getPatient().getEmail());
        patientDobPicker.setValue(appointment.getPatient().getDob());
       // queueNumberField.setText(appointment.getRegistrationNumber());
        roomNameField.setText(appointment.getRoom().getRoomNumber());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        examTimeField.setText(appointment.getAppointmentDate().format(formatter));
        doctorExaminesField.setText(appointment.getStaff().getName());
        symptomPresentationField.setText("Đau răng");
    }
}
