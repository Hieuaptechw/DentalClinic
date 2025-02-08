package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.entities.Appointment;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

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
    @FXML private TableColumn<Appointment, Void> actionColumn;

    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();

        setupTableColumns();
        loadAppointments(em);
    }

    private void setupTableColumns() {
        registrationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        patientColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getPatient().getName()));
        doctorColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getDoctor().getName()));
        roomColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getRoom().getRoomNumber()));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        actionColumn.setCellFactory(KParameter -> new TableCell<Appointment, Void>() {
            private final Button editButton;
            private final Button deleteButton;

            {
                // Tạo icon sửa
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/pen.png")));
                editIcon.setFitWidth(20);
                editIcon.setFitHeight(20);
                editButton = new Button("", editIcon); // Nút không có text, chỉ có icon
                editButton.setStyle("-fx-cursor: hand;");

                editButton.setOnAction(event -> {
                    Appointment appointment = getTableRow().getItem();
                    if (appointment != null) {
                        // loadViewEditProduct(inventory);
                    }
                });

                // Tạo icon xóa
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete.png")));
                deleteIcon.setFitWidth(20);
                deleteIcon.setFitHeight(20);
                deleteButton = new Button("", deleteIcon);
                deleteButton.setStyle("-fx-cursor: hand;");

                deleteButton.setOnAction(event -> {
                    Appointment appointment = getTableRow().getItem();
                    if (appointment != null) {
                        // handleDelete(inventory);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10, editButton, deleteButton);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    private void loadAppointments(EntityManager em) {
        List<Appointment> appointments = em.createQuery("SELECT a FROM Appointment a", Appointment.class).getResultList();
        System.out.println("Số lượng lịch hẹn: " + appointments.size());
        appointments.forEach(System.out::println);
        appointmentList.setAll(appointments);
        tableViewAppointment.setItems(appointmentList);
    }
}
