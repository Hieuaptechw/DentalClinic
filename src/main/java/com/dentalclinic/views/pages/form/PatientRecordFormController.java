package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.MedicalRecordController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.entities.Patient;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class PatientRecordFormController {
    @FXML
    private TextField nameField,diagnoseField,treatmentField, namePatient, diagnosePatient, treatmentPatient;

    @FXML
    private DatePicker datePatient;

    @FXML
    private Button btnAction;

    private MedicalRecordController patientRecordController;
    private MedicalRecord medicalRecord;

    public PatientRecordFormController(){
        EntityManager em = DatabaseController.getEntityManager();
        this.patientRecordController = new MedicalRecordController(em);
    }

    public void setPatientRecordData(MedicalRecord medicalRecord){
        this.medicalRecord = medicalRecord;
        nameField.setText(medicalRecord.getPatient().getName());
        diagnoseField.setText(medicalRecord.getDiagnosis());
        treatmentField.setText(medicalRecord.getTreatment());
    }

    @FXML
    private void handAddPatientRecord(){
        String name = namePatient.getText().trim();
        String diagnoses = diagnosePatient.getText().trim();
        String treatment = treatmentPatient.getText().trim();


        Patient patient = patientRecordController.findPatientByName(name);
        if(patient != null){
            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setPatient(patient);
            medicalRecord.setDiagnosis(diagnoses);
            medicalRecord.setTreatment(treatment);
            medicalRecord.setCreatedAt(LocalDateTime.now());
            patientRecordController.handleAddPatientRecord(medicalRecord);
            closeWindow();
        }else {
            alertMessage(Alert.AlertType.ERROR, "Đã xảy ra lỗi", "Không tìm thấy bệnh nhân: " + name);
        }
    }

    @FXML
    private void handleSave(){
        if(medicalRecord != null){
            String diagnose = diagnoseField.getText().trim();
            String treatment = treatmentField.getText().trim();

            medicalRecord.setDiagnosis(diagnose);
            medicalRecord.setTreatment(treatment);

            patientRecordController.updatePatientRecord(medicalRecord);
            alertMessage(Alert.AlertType.INFORMATION, "Cập nhật bệnh án", "Bệnh án đã được cập nhật");

            closeWindow();
        }
    }

    public void alertMessage(Alert.AlertType type, String title, String headerText){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }


    public void closeWindow(){
        Stage stage = (Stage) btnAction.getScene().getWindow();
        stage.close();
    }


}
