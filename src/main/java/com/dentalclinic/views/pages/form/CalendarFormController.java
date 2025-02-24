package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.CalendarController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.Appointment;
import com.dentalclinic.entities.Room;
import com.dentalclinic.entities.Staff;
import jakarta.persistence.EntityManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CalendarFormController {
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
    private DatePicker appointmentDatePicker;
    @FXML
    private TextField timeField;
    @FXML
    private TextField examTimeField;
    @FXML
    private TextArea symptomPresentationField;
    @FXML
    private ComboBox<Room> comboBoxRoom;
    @FXML
    private ComboBox<Staff> comboBoxDoctor;
    @FXML
    private Button btnAction;
    private CalendarController calendarController;
    private Appointment appointment;
    private ObservableList<Staff> doctorList;
    private ObservableList<Room> roomList;

    @FXML
    public void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        calendarController = new CalendarController(em);
    }

    public void setComboBox(ObservableList<Staff> doctorList, ObservableList<Room> roomList) {
        this.doctorList = doctorList;
        this.roomList = roomList;

        comboBoxDoctor.setItems(doctorList);
        comboBoxRoom.setItems(roomList);

        comboBoxDoctor.setConverter(new StringConverter<Staff>() {
            @Override
            public String toString(Staff doctor) {
                return doctor != null ? doctor.getName() : "";
            }

            @Override
            public Staff fromString(String string) {
                return null;
            }
        });

        comboBoxRoom.setConverter(new StringConverter<Room>() {
            @Override
            public String toString(Room room) {
                return room != null ? room.getRoomNumber() + " - " + room.getRoomType() : "";
            }

            @Override
            public Room fromString(String string) {
                return null;
            }
        });
    }

    public void setInforAppointment(Appointment appointment) {
        this.appointment = appointment;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        patientNameField.setText(appointment.getPatient().getName());
        patientGenderField.setText(appointment.getPatient().getGender().toString());
        patientPhoneField.setText(appointment.getPatient().getPhone());
        patientAddressArea.setText(appointment.getPatient().getAddress());
        patientEmailField.setText(appointment.getPatient().getEmail());
        patientDobPicker.setValue(appointment.getPatient().getDob());

        LocalDateTime appointmentDateTime = appointment.getAppointmentDate();
        timeField.setText(appointmentDateTime.format(timeFormatter));
        appointmentDatePicker.setValue(appointmentDateTime.toLocalDate());
        String symptom = (appointment.getSymptoms() != null) ? appointment.getSymptoms() : "N/A";
        symptomPresentationField.setText(symptom);


        comboBoxDoctor.setValue(appointment.getStaff());
        comboBoxRoom.setValue(appointment.getRoom());
    }
    @FXML
    private void handleUpdateAppointment() {
        Staff selectedDoctor = comboBoxDoctor.getSelectionModel().getSelectedItem();
        Room selectedRoom = comboBoxRoom.getSelectionModel().getSelectedItem();
        LocalDate appointmentDate = appointmentDatePicker.getValue();
        LocalDateTime appointmentDateTime = appointment.getAppointmentDate();
        String symptoms = symptomPresentationField.getText();
        String time = timeField.getText();
        if (selectedDoctor == null || selectedRoom == null || appointmentDate == null || symptoms.isEmpty() || time.isEmpty()) {
            System.out.println("Please fill in all required fields!");
            return;
        }

        LocalTime appointmentTime;
        try {
            appointmentTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid time format. Please enter HH:mm!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        LocalDateTime newAppointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
        appointment.setAppointmentDate(newAppointmentDateTime);
        appointment.setStaff(selectedDoctor);
        appointment.setRoom(selectedRoom);
        appointment.setSymptoms(symptoms);
        System.out.println(appointment);
        calendarController.updateAppointment(appointment);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment updated successfully!", ButtonType.OK);
        alert.showAndWait();
        Stage stage = (Stage) btnAction.getScene().getWindow();
        stage.close();
    }

}
