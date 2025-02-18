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
        String patient = medicalRecord.getPatient().getName();
        nameLabel.setText(patient);
        genderLabel.setText(medicalRecord.getPatient().getGender().toString());
        addressLabel.setText(medicalRecord.getPatient().getAddress());
        dobLabel.setText(medicalRecord.getPatient().getDob().toString());
        indentityCardLabel.setText(medicalRecord.getPatient().getIdentityCard());
        emailLabel.setText(medicalRecord.getPatient().getEmail());
        dovLabel.setText(medicalRecord.getDateOfVisit().toString());
        dobLabel.setText(medicalRecord.getPatient().getAddress());
        phoneLabel.setText(medicalRecord.getPatient().getPhone());
        recordCodeLabel.setText("00" + medicalRecord.getRecordId());
        reasonLabel.setText(medicalRecord.getReason());
        diagnosisLabel.setText(medicalRecord.getDiagnosis());
        treatmentLabel.setText(medicalRecord.getTreatment());
        noteLabel.setText(medicalRecord.getNotes());
        followUpdatLabel.setText(medicalRecord.getFollowUpDate().toString());
        List<MedicalRecordMedicine> medicalRecordMedicines =
                medicalRecordMedicineController.getMedicinesByRecordId(medicalRecord.getRecordId());

        String medicineList = medicalRecordMedicines.stream()
                .map(m -> m.getMedicine().getName())
                .collect(Collectors.joining(", "));
        medicineLabel.setText(medicineList);
        String doctor = medicalRecord.getDoctor().getName();
        doctorLabel.setText(doctor);
        String[] patientNameParts = patient.split(" ");
        String signaturePatient = patientNameParts[patientNameParts.length - 1];

        String[] doctorNameParts = doctor.split(" ");
        String signatureDoctor = doctorNameParts[doctorNameParts.length - 1];
        signatureDoctorLabel.setText(signatureDoctor);
        signaturePatientLabel.setText(signaturePatient);
        fullNameDoctorLabel.setText(doctor);
        fullNamePatientLabel.setText(patient);



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
