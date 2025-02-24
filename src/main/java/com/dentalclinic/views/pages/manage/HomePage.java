package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.*;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.util.List;

@Page(name="Home", icon="images/home.png", fxml="manage/home.fxml")
public class HomePage extends AbstractPage {
    @FXML private Label allDoctor;
    @FXML private VBox doctorListContainer;
    @FXML private VBox patientNewListContainer;

    @FXML private LineChart<String, Number> patientLineChart;
    @FXML private BarChart<String, Number> examinationBarChart;
    @FXML private Label allPatient;
    @FXML private Label allExamination;
    @FXML private Label allAppointment;
    private StaffController staffController;
    private ExaminationRecordController examinationRecordController;
    private AppointmentController appointmentController;
    private PatientController patientController;
    @FXML
    private void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        staffController = new StaffController(em);
        patientController = new PatientController(em);
        examinationRecordController = new ExaminationRecordController(em);
        appointmentController = new AppointmentController(em);
        loadData();
        loadPatientChart();
        loadExaminationChart();
        loadDoctorList();
        loadPatientList();
    }
    public void loadData(){
        allDoctor.setText( String.valueOf(staffController.countDoctors()));;
        allPatient.setText(String.valueOf(patientController.countPatients()));;
        allAppointment.setText(String.valueOf(appointmentController.countAppointments()));;
        allExamination.setText(String.valueOf(examinationRecordController.countExamination()));
    }
    public void loadPatientChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Patients Per Month");

        List<Object[]> data = patientController.getPatientsPerMonth();
        for (Object[] row : data) {
            int month = (int) row[0];
            long count = (long) row[1];
            series.getData().add(new XYChart.Data<>(getMonthName(month), count));
        }

        patientLineChart.getData().clear();
        patientLineChart.getData().add(series);
    }
    public void loadExaminationChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Examinations per Doctor");

        List<Object[]> data = examinationRecordController.getExaminationsPerDoctor();
        for (Object[] row : data) {
            String doctorName = (String) row[0];
            long examCount = (long) row[1];
            series.getData().add(new XYChart.Data<>(doctorName, examCount));
        }

        examinationBarChart.getData().clear();
        examinationBarChart.getData().add(series);
    }
    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month - 1];
    }
    private void loadDoctorList() {
        List<Staff> doctors = staffController.getDoctors();
        doctorListContainer.getChildren().clear();

        for (Staff doctor : doctors) {
            HBox doctorBox = new HBox(10);
            doctorBox.setStyle("-fx-border-color: #ccc; -fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-padding: 10;");

            Label nameLabel = new Label(doctor.getName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label specialtyLabel = new Label("Specialty: " + doctor.getSpecialty());
            specialtyLabel.setStyle("-fx-font-size: 12px;");

            doctorBox.getChildren().addAll(nameLabel, specialtyLabel);
            doctorListContainer.getChildren().add(doctorBox);
        }
    }
    private void loadPatientList() {
        List<Patient> patients = patientController.getLatestPatients();
        patientNewListContainer.getChildren().clear();

        for (Patient patient : patients) {
            HBox patientBox = new HBox(15);
            patientBox.setStyle("-fx-border-color: #ccc; -fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-padding: 10; -fx-alignment: center-left;");

            Label nameLabel = new Label("Name: " + patient.getName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label genderLabel = new Label("Gender: " + patient.getGender());
            genderLabel.setStyle("-fx-font-size: 12px;");

            Label phoneLabel = new Label("Phone: " + patient.getPhone());
            phoneLabel.setStyle("-fx-font-size: 12px;");

            Label dobLabel = new Label("Date of Birth: " + patient.getDob());
            dobLabel.setStyle("-fx-font-size: 12px;");

            Label addressLabel = new Label("Address: " + patient.getAddress());
            addressLabel.setStyle("-fx-font-size: 12px;");

            Label createdAtLabel = new Label("Registration Date: " + patient.getCreatedAt());
            createdAtLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

            patientBox.getChildren().addAll(nameLabel, genderLabel, phoneLabel, dobLabel, addressLabel, createdAtLabel);
            patientNewListContainer.getChildren().add(patientBox);
        }

    }


}
