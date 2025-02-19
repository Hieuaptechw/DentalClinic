package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.*;
import com.dentalclinic.entities.*;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;



public class ExaminationFormController {
    @FXML
    private TextField nameField, addressField, identityField, dateField, birthField, phoneField, reasonField, symptimField;

    @FXML
    private Button btnAction;

    @FXML
    private ComboBox<Staff> doctorBox;

    @FXML
    private ComboBox<Room> roomBox;

    private Patient selectedPatient;
    private ExaminationRecord selectedExamination;

    private ExaminationRecordController examinationRecordController;
    private StaffController staffController;
    private RoomController roomController;
    private PatientController patientController;
    public void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        staffController = new StaffController(em);
        roomController = new RoomController(em);
        examinationRecordController = new ExaminationRecordController(em);
        patientController = new PatientController(em);
        loadDoctors();
        loadRooms();
    }

    private void loadDoctors() {
        List<Staff> doctors = staffController.getDoctors();
        if (doctors != null && !doctors.isEmpty()) {
            doctorBox.setItems(FXCollections.observableArrayList(doctors));
        }
    }

    private void loadBrands(){
        List<Branch>
    }


    private void loadRooms() {
        List<Room> rooms = roomController.getAllRooms();
        if (rooms != null && !rooms.isEmpty()) {
            roomBox.setItems(FXCollections.observableArrayList(rooms));
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
            dateField.setText(patient.getCreatedAt().format(formatter));
        }
        if (patient.getDob() != null) {
            birthField.setText(patient.getDob().format(formatter));
        }
    }

    public void setExamination(ExaminationRecord examinationRecord){
        System.out.println(examinationRecord);
        nameField.setText(examinationRecord.getPatient().getName());
        addressField.setText(examinationRecord.getPatient().getAddress());
        identityField.setText(examinationRecord.getPatient().getIdentityCard());
        phoneField.setText(examinationRecord.getPatient().getPhone());
        birthField.setText(examinationRecord.getPatient().getDob().toString());
        dateField.setText(examinationRecord.getDateOfVisit().toString());
        reasonField.setText(examinationRecord.getReason());
        symptimField.setText(examinationRecord.getSymptoms());
        doctorBox.setValue(examinationRecord.getStaff());
        roomBox.setValue(examinationRecord.getRoom());
        btnAction.setDisable(true);
        doctorBox.setDisable(true);
        roomBox.setDisable(true);
        reasonField.setEditable(false);
        symptimField.setEditable(false);
    }



        @FXML
        private void handleConfirm(){
            String symptoms = symptimField.getText().trim();
            String reason = reasonField.getText().trim();
            Staff selectedDoctor = doctorBox.getValue();
            Room selectedRoom = roomBox.getValue();


            Long patientId = selectedPatient.getPatientId();
            Patient patients = patientController.getPatientById(patientId);

            ExaminationRecord record = new ExaminationRecord();
            record.setPatient(patients);
            record.setStaff(selectedDoctor);
            record.setRoom(selectedRoom);
            record.setReason(reason);
            record.setSymptoms(symptoms);
            record.setDateOfVisit(LocalDateTime.now());

            examinationRecordController.createExaminationRecord(record);
            alertMessage(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu thông tin khám!", null);
            closeStage();
        }

    public void closeStage() {
        Stage stage = (Stage) btnAction.getScene().getWindow();
        stage.close();
    }


    public void alertMessage(Alert.AlertType type, String title, String text, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.setContentText(content);
        alert.show();
    }

}
