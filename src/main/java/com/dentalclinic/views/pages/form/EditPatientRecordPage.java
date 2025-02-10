package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientRecordController;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.entities.Patient;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;


public class EditPatientRecordPage {
    @FXML
    private TextField diagnoseField;

    @FXML
    private TextField treatmentField;

    @FXML
    private Button btnAction;

    private PatientRecordController patientRecordController;
    private MedicalRecord medicalRecord;

    public EditPatientRecordPage(){
        EntityManager em = DatabaseController.getEntityManager();
        this.patientRecordController = new PatientRecordController(em);
    }

    public void setPatientRecordData(MedicalRecord medicalRecord){
        this.medicalRecord = medicalRecord;
        diagnoseField.setText(medicalRecord.getDiagnosis());
        treatmentField.setText(medicalRecord.getTreatment());
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

            Stage stage = (Stage) btnAction.getScene().getWindow();
            stage.close();
        }
    }

    public void alertMessage(Alert.AlertType type, String title, String headerText){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }



}
