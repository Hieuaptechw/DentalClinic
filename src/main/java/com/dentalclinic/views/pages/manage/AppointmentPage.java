package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.AppointmentController;
import com.dentalclinic.controllers.CalendarController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.entities.*;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.AppointmentFormController;
import com.dentalclinic.views.pages.form.CalendarFormController;
import com.dentalclinic.views.pages.form.PatientFormController;
import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Page(name = "Appointment", icon = "images/calendar.png", fxml = "manage/calendar.fxml")
public class AppointmentPage extends AbstractPage {

    @FXML private TableView<Appointment> tableViewAppointment;
    @FXML private TableColumn<Appointment, String> reasonColumn, patientColumn, patientIndentityCardColumn, doctorColumn, roomColumn, statusColumn, symptomsColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> appointmentDateColumn;
    @FXML private TableColumn<Appointment, Void> actionColumn;
    @FXML private ComboBox<Room> comboBoxRoom;
    @FXML private ComboBox<Staff> comboBoxDoctor;
    @FXML private DatePicker datePickerAppointment;
    @FXML private TextArea symptomsArea;
    @FXML private TextField timeField, searchField;
    @FXML private DatePicker fromDatePicker, toDatePicker;
    private List<Room> roomList;
    private List<Staff> doctorList;
    private AppointmentController appointmentController;
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        appointmentController = new AppointmentController(em);
        setupTableColumns();
        loadData();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    private void setupTableColumns() {
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        patientIndentityCardColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getPatient().getIdentityCard()));
        patientColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getPatient().getName()));
        doctorColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getStaff().getName()));
        roomColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getRoom().getRoomNumber()));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        symptomsColumn.setCellValueFactory(new PropertyValueFactory<>("symptoms"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadActionColumn();

    }
    private void loadActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton;
            private final Button deleteButton;
            private final Button viewButton;
            private final HBox buttonBox;

            {
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/edit.png")));
                editIcon.setFitHeight(22);
                editIcon.setFitWidth(22);

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete_1.png")));
                deleteIcon.setFitHeight(22);
                deleteIcon.setFitWidth(22);

                ImageView viewIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/eye.png")));
                viewIcon.setFitHeight(22);
                viewIcon.setFitWidth(22);

                editButton = new Button("", editIcon);
                deleteButton = new Button("", deleteIcon);
                viewButton = new Button("", viewIcon);

                editButton.getStyleClass().add("button-icon");
                deleteButton.getStyleClass().add("button-icon");
                viewButton.getStyleClass().add("button-icon");

                editButton.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    handleEditAppointment(appointment);
                });

                deleteButton.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    handleDeleteAppointment(appointment);
                });

                viewButton.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    handleViewAppointment(appointment);
                });

                buttonBox = new HBox(5, editButton, viewButton, deleteButton);
                buttonBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                RoleType role = UserSession.getCurrentUserRole();

                if (empty) {
                    setGraphic(null);
                } else {
                    if (role == RoleType.ADMIN) {
                        buttonBox.getChildren().setAll(editButton, viewButton, deleteButton);
                    } else if (role == RoleType.DOCTOR) {
                        buttonBox.getChildren().setAll( viewButton);
                    } else {
                        buttonBox.getChildren().setAll(viewButton,editButton);
                    }
                    setGraphic(buttonBox);
                }
            }
        });
    }

    @FXML
    private void handleFilter(){
        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            tableViewAppointment.setItems(FXCollections.observableArrayList(appointmentList));
            loadActionColumn();
            return;
        }
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        if (toDate.isBefore(fromDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "End date must be greater than or equal to the start date!", ButtonType.OK);
            alert.showAndWait();

            return;
        }
        List<Appointment> filterList = appointmentList.stream()
                .filter(a->{
                    LocalDateTime appointmentDate = a.getAppointmentDate();
                    return !appointmentDate.toLocalDate().isBefore(fromDate) && appointmentDate.toLocalDate().isAfter(toDate);
                }).toList();
        tableViewAppointment.setItems(FXCollections.observableArrayList(filterList));
        loadActionColumn();
        if (filterList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No appointment found within this time period!", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Found " + filterList.size() + " Appointment!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    @FXML
    private void handleSearch(String keyword) {
        String trimmedKeyword = keyword.toLowerCase().trim();

        if (trimmedKeyword.isEmpty()) {
            tableViewAppointment.setItems(appointmentList);
        } else {
            List<Appointment> filteredList = appointmentList.stream()
                    .filter(appointment -> matchesSearch(appointment, trimmedKeyword))
                    .toList();

            tableViewAppointment.setItems(FXCollections.observableArrayList(filteredList));
        }

        loadActionColumn();
    }

    private boolean matchesSearch(Appointment appointment, String keyword) {
        String[] keywords = keyword.split("\\s+");

        String combinedData = (appointment.getStaff().getName() + " " +
                appointment.getPatient().getIdentityCard() + " " +
                appointment.getPatient().getName()).toLowerCase();

        return Arrays.stream(keywords).allMatch(combinedData::contains);
    }

    private void handleDeleteAppointment(Appointment appointment) {
        String message = "Appointment Information:\n" +
                "Registration Number: " + appointment.getAppointmentId() + "\n" +
                "Patient: " + appointment.getPatient().getName() + "\n" +
                "Doctor: " + appointment.getStaff().getName() + "\n" +
                "Room: " + appointment.getRoom().getRoomNumber() + "\n" +
                "Reason: " + appointment.getReason() + "\n" +
                "Appointment Date & Time: " + appointment.getAppointmentDate() + "\n" +
                "Symptoms: " + appointment.getSymptoms() + "\n" +
                "Status: " + appointment.getStatus();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this appointment?");
        alert.setContentText(message);

        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                appointmentController.deleteAppointment(appointment.getAppointmentId());
                loadData();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Appointment deleted successfully!", ButtonType.OK);
                alert1.showAndWait();
            }
        });
    }

    private void handleEditAppointment(Appointment appointment) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/appointmentform.fxml"));
            Parent root = loader.load();
            AppointmentFormController controller = loader.getController();
            controller.setAppointment(appointment);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Appointment");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadData();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private void handleViewAppointment(Appointment appointment) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/appointmentform.fxml"));
            Parent root = loader.load();
            AppointmentFormController controller = loader.getController();
            controller.setAppointmentView(appointment);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Appointment");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadData();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private void loadData(){
        List<Appointment> appointments = appointmentController.getAppointments().stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentDate).reversed())
                .collect(Collectors.toList());

        appointmentList.setAll(appointments);
        tableViewAppointment.setItems(appointmentList);

    }


}
