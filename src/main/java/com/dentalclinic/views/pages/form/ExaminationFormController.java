package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.*;
import com.dentalclinic.entities.*;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;



public class ExaminationFormController {
    @FXML
    private TextField nameField, addressField, identityField, birthField, phoneField, reasonField, symptimField,timeField;
    @FXML private TextField diagnosisField,treatmentField,searchMedicine;
    @FXML private DatePicker followUpdatePicker;
    @FXML private TextArea noteArea;
    @FXML private ComboBox<PatientStatus> patientStatusComboBox;
    @FXML
    private DatePicker dateField;
@FXML
private Button addMedicineButton;

    @FXML
    private Button btnAction;

    @FXML
    private ComboBox<Staff> doctorBox;
    @FXML
    private ComboBox<ExaminationStatus> statusComboBox;
    @FXML
    private ComboBox<String> roomBox;
    @FXML
    private ComboBox<String> medicineComboBox;
    @FXML
    private ListView<String> selectedMedicinesListView;
    private Patient selectedPatient;
    private ExaminationRecord selectedExamination;
    private ObservableList<String> allMedicines = FXCollections.observableArrayList();
    private ObservableList<String> selectedMedicines = FXCollections.observableArrayList();
    private ExaminationRecordController examinationRecordController;
    private StaffController staffController;
private PatientController patientController;
private MedicineController medicineController;
private MedicalRecordController medicalRecordController;
private Staff user;
    public void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        user = UserSession.getCurrentUser();
        staffController = new StaffController(em);
        medicineController = new MedicineController(em);
        medicalRecordController = new MedicalRecordController(em);
        examinationRecordController = new ExaminationRecordController(em);
        patientController = new PatientController(em);

        if (user.getRole() == RoleType.DOCTOR) {
            addMedicineButton.setOnAction(event -> addSelectedMedicine());
            loadMedicineData(); // Gọi sau khi đã khởi tạo medicineController
            searchMedicine.textProperty().addListener((observable, oldValue, newValue) -> {
                filterMedicineList(newValue);
            });
        }

        loadData();
    }

    private void loadMedicineData() {
        List<Medicine> medicines = medicineController.getAllMedicine();
        allMedicines.setAll(medicines.stream().map(Medicine::getName).toList());
        medicineComboBox.setItems(allMedicines);
    }
    private void  loadData(){
        List<Staff> doctors = staffController.getDoctors();
        if (doctors != null && !doctors.isEmpty()) {
            doctorBox.setItems(FXCollections.observableArrayList(doctors));
        }
        roomBox.getItems().addAll("VIP","Standard");
        statusComboBox.getItems().addAll(ExaminationStatus.values());
        patientStatusComboBox.getItems().addAll(PatientStatus.values());
    }




    private void filterMedicineList(String keyword) {
        if (keyword.isEmpty()) {
            medicineComboBox.setItems(allMedicines);
        } else {
            List<String> filteredList = allMedicines.stream()
                    .filter(name -> name.toLowerCase().contains(keyword.toLowerCase()))
                    .limit(10)
                    .toList();
            medicineComboBox.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    public void setPatientData(Patient patient) {
        this.selectedPatient = patient;
        if (patient == null) return;

        nameField.setText(patient.getName());
        addressField.setText(patient.getAddress());
        identityField.setText(patient.getIdentityCard());
        phoneField.setText(patient.getPhone());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (patient.getCreatedAt() != null) {
            dateField.setValue(patient.getCreatedAt().toLocalDate());
        }
        if (patient.getDob() != null) {
            birthField.setText(patient.getDob().format(formatter));
        }
        patientStatusComboBox.getSelectionModel().select(patient.getStatus());
        patientStatusComboBox.setDisable(true);
    }
    public void setExaminationView(ExaminationRecord examinationRecord){
        this.selectedExamination = examinationRecord;
        this.selectedPatient = examinationRecord.getPatient();
        nameField.setText(examinationRecord.getPatient().getName());
        addressField.setText(examinationRecord.getPatient().getAddress());
        identityField.setText(examinationRecord.getPatient().getIdentityCard());
        phoneField.setText(examinationRecord.getPatient().getPhone());
        birthField.setText(examinationRecord.getPatient().getDob().toString());
        timeField.setText(examinationRecord.getDateOfVisit().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        dateField.setValue(examinationRecord.getDateOfVisit().toLocalDate());
        reasonField.setText(examinationRecord.getReason());
        symptimField.setText(examinationRecord.getSymptoms());
        statusComboBox.setValue(examinationRecord.getStatus());
        doctorBox.setValue(examinationRecord.getStaff());
        patientStatusComboBox.setValue(examinationRecord.getPatient().getStatus());
        roomBox.setValue(examinationRecord.getRoom().toString());
        btnAction.setDisable(true);
        doctorBox.setDisable(true);
        roomBox.setDisable(true);
        reasonField.setEditable(false);
        symptimField.setEditable(false);
        timeField.setEditable(false);
        statusComboBox.setDisable(true);
        patientStatusComboBox.setDisable(true);
    }
    public void setExamination(ExaminationRecord examinationRecord){
        this.selectedExamination = examinationRecord;
        this.selectedPatient = examinationRecord.getPatient();
        nameField.setText(examinationRecord.getPatient().getName());
        addressField.setText(examinationRecord.getPatient().getAddress());
        patientStatusComboBox.setValue(examinationRecord.getPatient().getStatus());
        identityField.setText(examinationRecord.getPatient().getIdentityCard());
        phoneField.setText(examinationRecord.getPatient().getPhone());
        birthField.setText(examinationRecord.getPatient().getDob().toString());
        dateField.setValue(examinationRecord.getDateOfVisit().toLocalDate());
        timeField.setText(examinationRecord.getDateOfVisit().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        reasonField.setText(examinationRecord.getReason());
        symptimField.setText(examinationRecord.getSymptoms());
        doctorBox.setValue(examinationRecord.getStaff());
        roomBox.setValue(examinationRecord.getRoom().toString());
        statusComboBox.setValue(examinationRecord.getStatus());
    }



    @FXML
    public void handleSaveExamination() {
        LocalDate examinationDate = dateField.getValue();
        String reason = reasonField.getText();
        String time = timeField.getText();
        String symptoms = symptimField.getText();
        Staff selectedDoctor = doctorBox.getValue();
        String selectedRoomType = roomBox.getValue();
        ExaminationStatus selectedStatus = statusComboBox.getValue();
        if (examinationDate == null || time.isEmpty() || selectedRoomType == null ) {

            return;
        }
        if (selectedDoctor == null) {
            selectedDoctor = examinationRecordController.getDoctorWithFewestAppointments();
            if (selectedDoctor == null) {

                return;
            }
        }


        LocalTime examinationTime;
        try {
            examinationTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {

            return;
        }
        LocalDateTime examinationDateTime = LocalDateTime.of(examinationDate, examinationTime);
        Room availableRoom = examinationRecordController.findAvailableRoom(selectedRoomType, examinationDateTime);
        if (availableRoom == null) {
            return;
        }
        System.out.println(availableRoom);
        if (selectedExamination != null) {
            selectedExamination.setDateOfVisit(examinationDateTime);
            selectedExamination.setReason(reason);
            selectedExamination.setStatus(selectedStatus);
            selectedExamination.setSymptoms(symptoms);
            selectedExamination.setStaff(selectedDoctor);
            selectedExamination.setRoom(availableRoom);
            selectedExamination.setUpdatedAt(LocalDateTime.now());
            examinationRecordController.updateExamination(selectedExamination);
            showAlert("Success", "Appointment updated successfully!", Alert.AlertType.INFORMATION);
            Stage stage = (Stage) doctorBox.getScene().getWindow();
            stage.close();
        } else {
            selectedStatus = ExaminationStatus.ONGOING;
            ExaminationRecord examinationRecord = new ExaminationRecord();
            examinationRecord.setPatient(selectedPatient);
            examinationRecord.setStaff(selectedDoctor);
            examinationRecord.setDateOfVisit(examinationDateTime);
            examinationRecord.setReason(reason);
            selectedExamination.setStatus(selectedStatus);
            examinationRecord.setSymptoms(symptoms);
            examinationRecord.setCreatedAt(LocalDateTime.now());
            examinationRecord.setUpdatedAt(LocalDateTime.now());
            examinationRecord.setRoom(availableRoom);
            examinationRecordController.createExaminationRecord(examinationRecord);
            showAlert("Success", "Appointment scheduled successfully!", Alert.AlertType.INFORMATION);
            Stage stage = (Stage) doctorBox.getScene().getWindow();
            stage.close();
        }
    }

    public void handleAddMedicalRecord() {
        String diagnosis = diagnosisField.getText().trim();
        String treatment = treatmentField.getText().trim();
        LocalDate fob = followUpdatePicker.getValue();
        String note = noteArea.getText().trim();
        LocalDate today = LocalDate.now();
        if (diagnosis.isEmpty() || treatment.isEmpty() || fob == null || note.isEmpty()) {
            showAlert("Error", "Please fill in all fields!", Alert.AlertType.ERROR);
            return;
        }
        if (fob.isBefore(today)) {
            showAlert("Error", "The follow-up date must be after today!", Alert.AlertType.ERROR);
            return;
        }
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setDiagnosis(diagnosisField.getText().trim());
        medicalRecord.setTreatment(treatmentField.getText().trim());
        medicalRecord.setFollowUpDate(followUpdatePicker.getValue());
        medicalRecord.setNotes(noteArea.getText().trim());
        medicalRecord.setUpdatedAt(LocalDateTime.now());
        medicalRecord.setCreatedAt(LocalDateTime.now());
        medicalRecord.setSymptoms(selectedExamination.getSymptoms());
        medicalRecord.setReason(selectedExamination.getReason());
        medicalRecord.setPatient(selectedPatient);
        medicalRecord.setDoctor(selectedExamination.getStaff());
        medicalRecord.setDateOfVisit(selectedExamination.getDateOfVisit().toLocalDate());
        Set<Medicine> selectedMedicinesSet = selectedMedicines.stream()
                .map(name -> medicineController.getMedicineByName(name))
                .collect(Collectors.toSet());
        medicalRecord.setMedicines(selectedMedicinesSet);
        PatientStatus patientStatus = patientStatusComboBox.getValue();
        ExaminationStatus selectedStatus = statusComboBox.getValue();
        if(selectedStatus == ExaminationStatus.NO_SHOW) {
            selectedExamination.setStatus(statusComboBox.getValue());
            examinationRecordController.updateExamination(selectedExamination);
            showAlert("Error", "The patient is not present!", Alert.AlertType.ERROR);
            Stage stage = (Stage) selectedMedicinesListView.getScene().getWindow();
            stage.close();
            return;
        } else if (selectedStatus == ExaminationStatus.ONGOING) {
            selectedExamination.setStatus(ExaminationStatus.COMPLETED);
            examinationRecordController.updateExamination(selectedExamination);
        }

        if (patientStatus != selectedPatient.getStatus()) {
            selectedPatient.setStatus(patientStatus);
            patientController.updatePatient(selectedPatient);
        }
        System.out.println(medicalRecord);
        medicalRecordController.handleAddMedicalRecord(medicalRecord);
        showAlert("Success", "The patient has been examined!", Alert.AlertType.INFORMATION);
        Stage stage = (Stage) selectedMedicinesListView.getScene().getWindow();
        stage.close();
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void addSelectedMedicine() {
        String selectedMedicine = medicineComboBox.getSelectionModel().getSelectedItem();
        if (selectedMedicine != null && !selectedMedicines.contains(selectedMedicine)) {
            selectedMedicines.add(selectedMedicine);
            selectedMedicinesListView.setItems(selectedMedicines);
        }
    }

}
