package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.ExaminationRecordController;
import com.dentalclinic.controllers.RoomController;
import com.dentalclinic.controllers.StaffController;
import com.dentalclinic.entities.ExaminationRecord;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.Room;
import com.dentalclinic.entities.Staff;
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
    private ComboBox<String> doctorBox, roomBox;

    private Patient selectedPatient;
    private ExaminationRecord selectedExamination;

    private ExaminationRecordController examinationRecordController;
    private StaffController staffController;
    private RoomController roomController;
    public void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        staffController = new StaffController(em);
        roomController = new RoomController(em);
        examinationRecordController = new ExaminationRecordController(em);
        loadDoctors();
        loadRooms();
    }

    private void loadDoctors() {
        List<Staff> doctors = staffController.getDoctors();
        if (doctors != null && !doctors.isEmpty()) {
            doctorBox.setItems(FXCollections.observableArrayList(
                    doctors.stream().map(Staff::getName).collect(Collectors.toList())
            ));
        }
    }

    private void loadRooms() {
        List<Room> rooms = roomController.getAllRooms();
        if (rooms != null && !rooms.isEmpty()) {
            roomBox.setItems(FXCollections.observableArrayList(
                    rooms.stream().map(Room::getRoomType).collect(Collectors.toList())
            ));
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
        doctorBox.setValue(examinationRecord.getStaff().getName());
        roomBox.setValue(examinationRecord.getRoom().getRoomNumber());
        btnAction.setDisable(true);
        doctorBox.setDisable(true); // Vô hiệu hóa ComboBox bác sĩ
        roomBox.setDisable(true);  // Cho phép chọn phòng
        reasonField.setEditable(false); // Cho phép nhập lý do khám
        symptimField.setEditable(false);
    }



    @FXML
    private void handleConfirm(){
        EntityManager em = DatabaseController.getEntityManager();
        String symptoms = symptimField.getText().trim();
        String reason = reasonField.getText().trim();
        String doctorName = doctorBox.getValue();
        String roomType = roomBox.getValue();
        if(doctorName !=null){
            
        }


        String patientName = nameField.getText().trim();
        Patient patient = em.createQuery("SELECT p FROM Patient p WHERE p.name = :name", Patient.class)
                .setParameter("name", patientName)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        Staff staff = em.createQuery("SELECT s FROM Staff s WHERE s.name = :name", Staff.class)
                .setParameter("name", doctorName)
                .getResultList()
                .stream()
                .findFirst().orElse(null);

        Room room = em.createQuery("SELECT r FROM Room r WHERE r.roomType = :type", Room.class)
                .setParameter("type", roomType)
                .getResultList()
                .stream()
                .findFirst().orElse(null);

        ExaminationRecord examinationRecord = new ExaminationRecord(patient, staff, LocalDateTime.now(), LocalDateTime.now(), reason, symptoms, room);
        examinationRecordController.saveRecord(examinationRecord);
        alertMessage(Alert.AlertType.INFORMATION, "Thành công", "Xác nhận", "Chuyển hồ sơ tới bác sĩ " + staff + " thành công");
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
