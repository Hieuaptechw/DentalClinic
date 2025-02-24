package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.AppointmentController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.EmailController;
import com.dentalclinic.controllers.ExaminationRecordController;
import com.dentalclinic.entities.*;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.AppointmentFormController;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Page(name = "Appointment", icon = "images/calendar.png", fxml = "manage/calendar.fxml")
public class AppointmentPage extends AbstractPage {

    @FXML private TableView<Appointment> tableViewAppointment;
    @FXML private TableColumn<Appointment, String>  patientColumn, patientIndentityCardColumn, doctorColumn, roomColumn, statusColumn, symptomsColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> appointmentDateColumn;
    @FXML private TableColumn<Appointment, Void> checkInColumn;
    @FXML private TableColumn<Appointment, Void> semdEmailColumn;
    @FXML private TableColumn<Appointment, Void> actionColumn;
    @FXML private ComboBox<AppointmentStatus> statusComboBox;
    @FXML private TextField  searchField;
    private AppointmentController appointmentController;
    private ExaminationRecordController examinationRecordController;
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        statusComboBox.setValue(AppointmentStatus.PENDING);
        appointmentController = new AppointmentController(em);
        examinationRecordController = new ExaminationRecordController(em);
        setupTableColumns();
        loadData();
        statusComboBox.setOnAction(event -> loadData());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    private void setupTableColumns() {
        patientIndentityCardColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getPatient().getIdentityCard()));
        patientColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getPatient().getName()));
        doctorColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getStaff().getName()));
        roomColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().getRoom().getRoomNumber()));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        symptomsColumn.setCellValueFactory(new PropertyValueFactory<>("symptoms"));
        statusComboBox.getItems().addAll(AppointmentStatus.values());
        loadCheckInColumn();
        loadSendEmailColumn();
        loadActionColumn();

    }
    private void loadSendEmailColumn() {
        semdEmailColumn.setCellFactory(param -> new TableCell<>() {
            private final Button contactButton = new Button("Send Email");

            {
                contactButton.getStyleClass().add("button-contact");
                contactButton.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    sendReminderToPatient(appointment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(contactButton);
                }
            }
        });
    }

    private void loadCheckInColumn() {
        checkInColumn.setCellFactory(param -> new TableCell<>() {
            private final Button arrivedButton;
            private final Button absentButton;
            private final Button contactButton;
            private final Label statusLabel;

            {
                ImageView tickIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/check.png")));
                tickIcon.setFitHeight(22);
                tickIcon.setFitWidth(22);
                ImageView xIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/x.png")));
                xIcon.setFitHeight(22);
                xIcon.setFitWidth(22);
                arrivedButton = new Button("", tickIcon);
                arrivedButton.getStyleClass().add("button-icon");
                absentButton = new Button("", xIcon);
                absentButton.getStyleClass().add("button-icon");

                contactButton = new Button("Call / Email");
                contactButton.getStyleClass().add("button-contact");

                statusLabel = new Label();
                statusLabel.getStyleClass().add("status-label");
                arrivedButton.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    handleCheckInAppointment(appointment, true);
                });

                absentButton.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    handleCheckInAppointment(appointment, false);
                });

                contactButton.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                      sendReminderToPatient(appointment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Appointment appointment = getTableView().getItems().get(getIndex());

                    if (appointment != null) {
                        HBox buttonContainer = new HBox(10);
                        buttonContainer.setAlignment(Pos.CENTER);

                        switch (appointment.getStatus()) {
                            case PENDING:
                                buttonContainer.getChildren().addAll(arrivedButton, absentButton);
                                break;
                            case COMPLETED:
                                statusLabel.setText("Completed");
                                statusLabel.getStyleClass().add("status-completed");
                                buttonContainer.getChildren().add(statusLabel);

                                break;
                            case CANCELLED:
                                statusLabel.setText("Cancelled");
                                statusLabel.getStyleClass().add("status-cancelled");
                                buttonContainer.getChildren().add(statusLabel);

                        }
                        setGraphic(buttonContainer);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }
    private void sendReminderToPatient(Appointment appointment) {
        String emailContent;
        String subject;

        if (appointment.getStatus() == AppointmentStatus.PENDING) {
            emailContent = EmailController.getAppointmentReminderTemplate(
                    appointment.getPatient().getName(),
                    appointment.getAppointmentDate().toLocalDate().toString(),
                    appointment.getAppointmentDate().toLocalTime().toString()
            );
            subject = "📌 Dental Appointment Reminder";
        } else {
            emailContent = EmailController.getMissedAppointmentTemplate(
                    appointment.getPatient().getName(),
                    appointment.getAppointmentDate().toLocalDate().toString(),
                    appointment.getAppointmentDate().toLocalTime().toString()
            );
            subject = "⚠️ Missed Appointment Notification";
        }

        boolean isSent = EmailController.sendEmail(appointment.getPatient().getEmail(), subject, emailContent);

        Alert alert = new Alert(isSent ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(isSent ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(isSent ?
                "Email has been successfully sent to: " + appointment.getPatient().getName() :
                "Failed to send email. Please check again.");

        alert.showAndWait();
    }

    private void handleCheckInAppointment(Appointment appointment, boolean checkIn) {
        if (appointment == null) return;
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        String headerText = checkIn ? "Are you sure the patient has arrived?" : "Are you sure the patient has not arrived?";
        confirmAlert.setTitle("Confirm Check-in");
        confirmAlert.setHeaderText(headerText);
        confirmAlert.setContentText("Name: " + appointment.getPatient().getName());

        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmAlert.getButtonTypes().setAll(buttonYes, buttonNo);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                appointment.setStatus(checkIn ? AppointmentStatus.COMPLETED : AppointmentStatus.CANCELLED);
                appointmentController.updateAppointment(appointment);
                if (checkIn) {
                    ExaminationRecord newExam = new ExaminationRecord();
                    newExam.setAppointment(appointment);
                    newExam.setPatient(appointment.getPatient());
                    newExam.setDateOfVisit(LocalDateTime.now());
                    newExam.setReason(appointment.getReason());
                    newExam.setStatus(ExaminationStatus.ONGOING);
                    newExam.setRoom(appointment.getRoom());
                    newExam.setStaff(appointment.getStaff());
                    newExam.setSymptoms(appointment.getSymptoms());
                    newExam.setCreatedAt(LocalDateTime.now());
                    newExam.setUpdatedAt(LocalDateTime.now());
                    examinationRecordController.createExaminationRecord(newExam);
                }
                loadData();
                Alert updateAlert = new Alert(Alert.AlertType.INFORMATION);
                updateAlert.setTitle("Appointment Updated");
                updateAlert.setHeaderText(null);
                updateAlert.setContentText("Appointment status updated successfully!");
                updateAlert.showAndWait();
            }
        });
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
        AppointmentStatus selectedStatus = statusComboBox.getValue();
        List<Appointment> appointments = appointmentController.getAppointments().stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentDate).reversed())
                .collect(Collectors.toList());

        List<Appointment> filteredAppointments = appointments.stream()
                .filter(a -> a.getStatus() == selectedStatus)
                .toList();

        if (selectedStatus == null) {
            appointmentList.setAll(appointments);
        } else {
            appointmentList.setAll(filteredAppointments);
        }

        tableViewAppointment.setItems(appointmentList);

    }


}
