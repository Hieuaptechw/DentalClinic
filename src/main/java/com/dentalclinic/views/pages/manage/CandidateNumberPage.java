package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.CandidateNumberController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.Appointment;
import com.dentalclinic.entities.Gender;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.CandidatenumberFormController;
import com.dentalclinic.views.pages.form.PatientFormController;
import jakarta.persistence.EntityManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.util.Duration;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Page(name="SBD", icon="images/waiting-room.png", fxml="manage/candidatenumber.fxml")
public class CandidateNumberPage extends AbstractPage {
    private final ObservableList<Appointment> appointmentListProcessing  = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointmentListWaitToday  = FXCollections.observableArrayList();
    private CandidateNumberController candidateNumberController;
    @FXML
    private Label timeLabel;
    @FXML
    private TableView<Appointment> tableViewAppointmentsToday;
    @FXML
    private TableView<Appointment> tablePatientsBeingExamined;
    @FXML
    private TableColumn<Appointment, String> colSBD_1  ;
    @FXML
    private TableColumn<Appointment, String> colName_1 ;
    @FXML
    private TableColumn<Appointment, String> colPriority_1 ;
    @FXML
    private TableColumn<Appointment, String> colStatus_1 ;
    @FXML
    private TableColumn<Appointment, String> colReason_1 ;
    @FXML
    private TableColumn<Appointment, Void> colAction_1;
    @FXML
    private TableColumn<Appointment, String> colSBD_2  ;
    @FXML
    private TableColumn<Appointment, String> colName_2 ;
    @FXML
    private TableColumn<Appointment, String> colRoom_2 ;
    @FXML
    private TableColumn<Appointment, String> colDoctor_2 ;
    @FXML
    private TableColumn<Appointment, String> colTime_2 ;
    @FXML
    private TableColumn<Appointment, Void> colAction_2;
    @FXML
    private TextField searchField;
    public void initialize() {
       updateTime();
       startClock();
       DatabaseController.init();
       EntityManager em = DatabaseController.getEntityManager();
       candidateNumberController = new CandidateNumberController(em);
       setupTableColumns();
       loadAppointmentsToday();
       searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
       startAutoUpdate();

   }
    private List<Appointment> currentProcessingAppointments = new ArrayList<>();
    private void loadAppointmentsToday() {
        List<Appointment> fetchedAppointments = candidateNumberController.getAllCandidateNumbers();
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        List<Appointment> todayAppointments = fetchedAppointments.stream()
                .filter(a -> a.getAppointmentDate() != null
                        && a.getAppointmentDate().toLocalDate().equals(today)
                        && currentProcessingAppointments.stream().noneMatch(p -> p.getRegistrationNumber().equals(a.getRegistrationNumber()))) // Không lấy trùng
                .collect(Collectors.toList());
        List<Appointment> appointmentListProcessingToday = todayAppointments.stream()
                .filter(a -> {
                    LocalTime appointmentTime = a.getAppointmentDate().toLocalTime();
                    return !currentTime.isBefore(appointmentTime) && currentTime.isBefore(appointmentTime.plusMinutes(45));
                })
                .toList();
        appointmentListWaitToday.setAll(todayAppointments);
        tableViewAppointmentsToday.setItems(appointmentListWaitToday);
        for (Appointment appointment : appointmentListProcessingToday) {
            if (currentProcessingAppointments.stream().noneMatch(p -> p.getRegistrationNumber().equals(appointment.getRegistrationNumber()))) {
                currentProcessingAppointments.add(appointment);
            }
        }

        appointmentListProcessing.setAll(currentProcessingAppointments);
        updateTables();
    }

    @FXML
    private void handleCallNext() {
        if (appointmentListWaitToday.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText("No patients in the waiting list!");
            alert.setContentText("Please check the appointment list again.");
            alert.show();
            return;
        }

        Appointment nextAppointment = appointmentListWaitToday.remove(0);
        currentProcessingAppointments.add(nextAppointment);
        appointmentListProcessing.setAll(currentProcessingAppointments);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Next Patient");
        alert.setHeaderText("Patient Information:");
        alert.setContentText(
                "Name: " + nextAppointment.getPatient().getName() + "\n" +
                        "Reg. Number: " + nextAppointment.getRegistrationNumber() + "\n" +
                        "Phone: " + nextAppointment.getPatient().getPhone() + "\n" +
                        "Appointment Time: " + nextAppointment.getAppointmentDate().toString() + "\n" +
                        "Doctor: " + nextAppointment.getStaff().getName()
        );
        alert.show();

        updateTables();
    }

    private void updateTables() {
        tableViewAppointmentsToday.setItems(FXCollections.observableArrayList(appointmentListWaitToday));
        tablePatientsBeingExamined.setItems(FXCollections.observableArrayList(currentProcessingAppointments));
    }
    private void startAutoUpdate() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(30), event -> loadAppointmentsToday()));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void setupTableColumns() {
        colSBD_1.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colReason_1.setCellValueFactory(new PropertyValueFactory<>("symptoms"));
        colStatus_1.setCellValueFactory(new PropertyValueFactory<>("status"));
        colName_1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatient().getName()));
        colPriority_1.setCellFactory(column -> new TableCell<>() {
            private final Label label = new Label();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setGraphic(null);
                } else {
                    Appointment appointment = getTableRow().getItem();
                    if (appointment != null) {
                        LocalDate dob = appointment.getPatient().getDob();
                        int age = calculateAge(dob);
                        Gender gender = appointment.getPatient().getGender();

                        String priorityClass = getPriorityClass(age, gender);
                        String priorityText = getPriorityLevel(age,gender);
                        label.setText(priorityText);
                        label.getStyleClass().add("lbl-" + priorityClass.toLowerCase().replace(" ", ""));
                        setGraphic(label);

                    }
                }
            }
        });

        loadActionColumn( colAction_1);
        colSBD_2.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colName_2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatient().getName()));
        colRoom_2.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getRoom().getRoomNumber()));
       colDoctor_2.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getStaff().getName()));
        colTime_2.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        loadActionColumn( colAction_2);
    }
    private void loadActionColumn(TableColumn<Appointment, Void> colNameActio) {
        colNameActio.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton;

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
                viewButton = new Button("", viewIcon);
                viewButton.getStyleClass().add("button-icon");

                viewButton.setOnAction(event -> {
                   Appointment appointment = getTableView().getItems().get(getIndex());
                    System.out.println(appointment);
                    handleView(appointment);
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5 , viewButton));
                }
            }

        });
    }


    private void handleView(Appointment appointment){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/candidatenumberform.fxml"));

            VBox root = loader.load();
            CandidatenumberFormController candidatenumberFormController = loader.getController();
            candidatenumberFormController.setInforPatient(appointment);
            Stage stage = new Stage();
            stage.setTitle("Information Patient");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void handleSearch(String keyword) {
        String trimmedKeyword = keyword.toLowerCase().trim();

        if (trimmedKeyword.isEmpty()) {
            tableViewAppointmentsToday.setItems(appointmentListWaitToday);
            tablePatientsBeingExamined.setItems(appointmentListProcessing);
        } else {
            List<Appointment> filteredWaitList = appointmentListWaitToday.stream()
                    .filter(appointment -> matchesSearch(appointment, trimmedKeyword))
                    .toList();
            List<Appointment> filteredProcessingList = appointmentListProcessing.stream()
                    .filter(appointment -> matchesSearch(appointment, trimmedKeyword))
                    .toList();
            tableViewAppointmentsToday.setItems(FXCollections.observableArrayList(filteredWaitList));
            tablePatientsBeingExamined.setItems(FXCollections.observableArrayList(filteredProcessingList));
        }

        loadActionColumn(colAction_1);
        loadActionColumn(colAction_2);
    }

    private boolean matchesSearch(Appointment appointment, String keyword) {
        String[] keywords = keyword.split("\\s+");

        String combinedData = (appointment.getPatient().getName() + " " +
                appointment.getRoom().getRoomNumber() + " " +
                appointment.getStaff().getName() + " " +
                appointment.getRegistrationNumber() + " " +
                appointment.getStatus()).toLowerCase();

        return Arrays.stream(keywords).allMatch(combinedData::contains);
    }

    private int calculateAge(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears();
    }
    private String getPriorityClass(int age, Gender gender) {
        if (age >= 65) {
            return "high-priority";
        } else if ((gender == Gender.FEMALE && age >= 50) || (gender == Gender.MALE && age >= 40)) {
            return "medium-priority";
        } else {
            return "low-priority";
        }
    }
    private String getPriorityLevel(int age, Gender gender) {
        if (age >= 65) {
            return "High";
        } else if ((gender == Gender.FEMALE && age >= 50) || (gender == Gender.MALE && age >= 40)) {
            return "Medium";
        } else {
            return "Low";
        }
    }


    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d-M-yyyy h:mm a", new Locale("vi", "VN"));
        String formattedTime = now.format(formatter);
        timeLabel.setText(formattedTime);
    }

    private void startClock() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


}
