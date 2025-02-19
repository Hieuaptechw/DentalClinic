package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.AppointmentController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.entities.*;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AppointmentFormController {
    @FXML
    private TextField nameField, addressField, identityField, phoneField, reasonField, timeField, symptomsField;
    @FXML
    private DatePicker datePicker, birthPicker;
    @FXML
    private ComboBox<Staff> staffComboBox;
    @FXML
    private ComboBox<Branch> branchComboBox;
    @FXML
    private ComboBox<String> roomComboBox;

    private Patient patientSelected;
    private List<Staff> doctorList;
    private List<Branch> branchList;
    private List<Room> roomList;
    private AppointmentController appointmentController;

    @FXML
    public void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        appointmentController = new AppointmentController(em);
        roomList =appointmentController.getRooms();
        setupComboBoxes();
    }
    public void setupComboBoxes() {

        doctorList = appointmentController.getStaffs()
                .stream()
                .filter(s -> s.getRole() == RoleType.DOCTOR)
                .toList();
        staffComboBox.getItems().addAll(doctorList);
        branchList = appointmentController.getBranches()
                        .stream().toList();
        ObservableList<Branch> branchObservableList = FXCollections.observableArrayList(branchList);
        branchComboBox.setItems(branchObservableList);
        branchComboBox.setConverter(new StringConverter<Branch>() {
            @Override
            public String toString(Branch branch) {
                return branch != null ? branch.getBranchName() : "";
            }

            @Override
            public Branch fromString(String string) {
                return null;
            }
        });
        roomComboBox.getItems().addAll("VIP", "Standard");
    }
    public void setAppointment(Patient patient) {
        this.patientSelected = patient;
        nameField.setText(patient.getName());
        addressField.setText(patient.getAddress());
        identityField.setText(patient.getIdentityCard());
        phoneField.setText(patient.getPhone());
        birthPicker.setValue(patient.getDob());
    }

    @FXML
    public void handleAddAppointment() {
        LocalDate appointmentDate = datePicker.getValue();
        String reason = reasonField.getText();
        String time = timeField.getText();
        String symptoms = symptomsField.getText();
        Staff selectedDoctor = staffComboBox.getValue();
        Branch selectedBranch = branchComboBox.getValue();
        String selectedRoomType = roomComboBox.getValue();

        if (appointmentDate == null || time.isEmpty() || selectedRoomType == null || selectedBranch == null) {
            showAlert("Error", "Please fill in all required fields!", Alert.AlertType.ERROR);
            return;
        }

        // Nếu chưa chọn bác sĩ, tìm bác sĩ có ít cuộc hẹn nhất
        if (selectedDoctor == null) {
            selectedDoctor = appointmentController.getDoctorWithFewestAppointments();
            if (selectedDoctor == null) {
                showAlert("Error", "No available doctors!", Alert.AlertType.ERROR);
                return;
            }
        }
        System.out.println(selectedDoctor);
        // Tìm phòng còn trống trong chi nhánh đã chọn
        Room availableRoom = appointmentController.findAvailableRoom( selectedRoomType, appointmentDate);
        if (availableRoom == null) {
            showAlert("Error", "No available rooms at this branch!", Alert.AlertType.ERROR);
            return;
        }
        System.out.println(availableRoom);
        LocalTime appointmentTime;
        try {
            appointmentTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            showAlert("Error", "Invalid time format! Use HH:mm", Alert.AlertType.ERROR);
            return;
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
        Appointment newAppointment = new Appointment();
        newAppointment.setPatient(patientSelected);
        newAppointment.setStaff(selectedDoctor);
        newAppointment.setAppointmentDate(appointmentDateTime);
        newAppointment.setReason(reason);
        newAppointment.setStatus("Scheduled");
        newAppointment.setSymptoms(symptoms);
        newAppointment.setCreatedAt(LocalDateTime.now());
        newAppointment.setUpdatedAt(LocalDateTime.now());
        newAppointment.setRoom(availableRoom);
        appointmentController.addAppointment(newAppointment);
        showAlert("Success", "Appointment scheduled successfully!", Alert.AlertType.INFORMATION);

    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
