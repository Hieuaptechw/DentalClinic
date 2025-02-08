package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.entities.Appointment;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;

@Page(name = "Lịch hẹn", icon = "images/calendar.png", fxml = "manage/calendar.fxml")
public class CalendarPage extends AbstractPage {

    @FXML private TableView<Appointment> tableViewAppointment;
    @FXML private TableColumn<Appointment, String> registrationNumberColumn;
    @FXML private TableColumn<Appointment, String> patientColumn;
    @FXML private TableColumn<Appointment, String> doctorColumn;
    @FXML private TableColumn<Appointment, String> roomColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;

    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();

        setupTableColumns();
        loadAppointments(em);
    }

    private void setupTableColumns() {
        registrationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));  // Assuming toString() gives useful info
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadAppointments(EntityManager em) {
        List<Appointment> appointments = em.createQuery("SELECT a FROM Appointment a", Appointment.class).getResultList();
        System.out.println("Số lượng lịch hẹn: " + appointments.size());
        appointments.forEach(System.out::println);
        appointmentList.setAll(appointments);
        tableViewAppointment.setItems(appointmentList);
    }
}
