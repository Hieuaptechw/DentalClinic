package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.MedicalRecordController;
import com.dentalclinic.controllers.MedicalRecordMedicineController;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.entities.MedicalRecordMedicine;
import com.dentalclinic.entities.Patient;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class PatientRecordFormController {
    @FXML
    private TextField nameField,diagnoseField,treatmentField, namePatient, diagnosePatient, treatmentPatient;
    @FXML
    private Label nameLabel,genderLabel,addressLabel,dobLabel,phoneLabel,emailLabel,recordCodeLabel,dovLabel,indentityCardLabel
            ,reasonLabel,diagnosisLabel,treatmentLabel,medicineLabel,noteLabel,followUpdatLabel,doctorLabel,signatureDoctorLabel
            ,signaturePatientLabel,fullNameDoctorLabel,fullNamePatientLabel;
    @FXML
    private DatePicker datePatient;

    @FXML
    private Button btnAction;
    private MedicalRecordMedicineController medicalRecordMedicineController;

    private MedicalRecordController patientRecordController;
    private MedicalRecord medicalRecord;


    public void setPatientRecordInformation(MedicalRecord medicalRecord) {

        nameLabel.setText(medicalRecord.getPatient().getName() != null ? medicalRecord.getPatient().getName() : "N/A");
        genderLabel.setText(medicalRecord.getPatient().getGender() != null ? medicalRecord.getPatient().getGender().toString() : "N/A");
        addressLabel.setText(medicalRecord.getPatient().getAddress() != null ? medicalRecord.getPatient().getAddress() : "N/A");
        dobLabel.setText(medicalRecord.getPatient().getDob() != null ? medicalRecord.getPatient().getDob().toString() : "N/A");
        indentityCardLabel.setText(medicalRecord.getPatient().getIdentityCard() != null ? medicalRecord.getPatient().getIdentityCard() : "N/A");
        emailLabel.setText(medicalRecord.getPatient().getEmail() != null ? medicalRecord.getPatient().getEmail() : "N/A");
        dovLabel.setText(medicalRecord.getDateOfVisit() != null ? medicalRecord.getDateOfVisit().toString() : "N/A");
        phoneLabel.setText(medicalRecord.getPatient().getPhone() != null ? medicalRecord.getPatient().getPhone() : "N/A");
        recordCodeLabel.setText(medicalRecord.getRecordId() > 0 ? "00" + medicalRecord.getRecordId() : "N/A");
        reasonLabel.setText(medicalRecord.getReason() != null ? medicalRecord.getReason() : "N/A");
        diagnosisLabel.setText(medicalRecord.getDiagnosis() != null ? medicalRecord.getDiagnosis() : "N/A");
        treatmentLabel.setText(medicalRecord.getTreatment() != null ? medicalRecord.getTreatment() : "N/A");
        noteLabel.setText(medicalRecord.getNotes() != null ? medicalRecord.getNotes() : "N/A");
        followUpdatLabel.setText(medicalRecord.getFollowUpDate() != null ? medicalRecord.getFollowUpDate().toString() : "N/A");
        List<MedicalRecordMedicine> medicalRecordMedicines =
                medicalRecordMedicineController.getMedicinesByRecordId(medicalRecord.getRecordId());
        String medicineList = (medicalRecordMedicines != null && !medicalRecordMedicines.isEmpty()) ?
                medicalRecordMedicines.stream().map(m -> m.getMedicine().getName()).collect(Collectors.joining(", ")) : "N/A";
        medicineLabel.setText(medicineList);
        String doctor = (medicalRecord.getDoctor() != null) ? medicalRecord.getDoctor().getName() : "N/A";
        doctorLabel.setText(doctor);
        String[] patientNameParts = medicalRecord.getPatient().getName().split(" ");
        String signaturePatient = patientNameParts.length > 0 ? patientNameParts[patientNameParts.length - 1] : "N/A";

        String[] doctorNameParts = doctor.split(" ");
        String signatureDoctor = doctorNameParts.length > 0 ? doctorNameParts[doctorNameParts.length - 1] : "N/A";

        signatureDoctorLabel.setText(signatureDoctor);
        signaturePatientLabel.setText(signaturePatient);
        fullNameDoctorLabel.setText(doctor);
        fullNamePatientLabel.setText(medicalRecord.getPatient().getName());
    }

    public PatientRecordFormController(){
        EntityManager em = DatabaseController.getEntityManager();
        this.patientRecordController = new MedicalRecordController(em);
        this.medicalRecordMedicineController = new MedicalRecordMedicineController(em);
    }

    public void setPatientRecordData(MedicalRecord medicalRecord){
        this.medicalRecord = medicalRecord;
        nameField.setText(medicalRecord.getPatient().getName());
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
            alertMessage(Alert.AlertType.INFORMATION, "Update Medical Record", "The medical record has been updated.");


            closeWindow();
        }
    }

    @FXML
    private void clearEdit(){
        diagnoseField.clear();
        treatmentField.clear();
    }

    @FXML
    private void clearAdd(){
        namePatient.clear();
        diagnosePatient.clear();
        treatmentField.clear();
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
