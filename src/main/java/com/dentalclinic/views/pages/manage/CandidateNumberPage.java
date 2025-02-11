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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
       startAutoUpdate();
       DatabaseController.init();
       EntityManager em = DatabaseController.getEntityManager();
       candidateNumberController = new CandidateNumberController(em);
       setupTableColumns();
       loadAppointmentsToday();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));

   }
    private void loadAppointmentsToday() {
        List<Appointment> fetchedAppointments = candidateNumberController.getAllCandidateNumbers();
        LocalDate today = LocalDate.now();

        // Lọc danh sách bệnh nhân hôm nay
        List<Appointment> todayAppointments = fetchedAppointments.stream()
                .filter(a -> a.getAppointmentDate().toLocalDate().equals(today))
                .toList();

        // Cập nhật danh sách chờ, nhưng không ảnh hưởng đến danh sách đang khám
        appointmentListWaitToday.setAll(todayAppointments.stream()
                .filter(a -> !appointmentListProcessing.contains(a)) // Không cập nhật bệnh nhân đang khám
                .toList());

        // Cập nhật giao diện
        updateTables();
    }

    private void autoCallNextPatient() {
        if (appointmentListProcessing.isEmpty()) {
            handleCallNext(); // Nếu không có ai đang khám, gọi bệnh nhân tiếp theo
            return;
        }

        // Lấy bệnh nhân hiện tại
        Appointment currentPatient = appointmentListProcessing.get(0);
        LocalDateTime appointmentStart = currentPatient.getAppointmentDate();
        LocalDateTime appointmentEnd = appointmentStart.plusMinutes(45);
        LocalDateTime currentTime = LocalDateTime.now();

        // Nếu hết thời gian khám, tự động gọi bệnh nhân tiếp theo
        if (currentTime.isAfter(appointmentEnd)) {
            appointmentListProcessing.remove(0); // Xóa bệnh nhân cũ khỏi danh sách đang khám
            handleCallNext(); // Gọi bệnh nhân tiếp theo
        }
    }

    private void updateTables() {
        tableViewAppointmentsToday.setItems(FXCollections.observableArrayList(appointmentListWaitToday));
        tablePatientsBeingExamined.setItems(FXCollections.observableArrayList(appointmentListProcessing));
    }
    private void setupTableColumns() {
        System.out.println(tableViewAppointmentsToday);
        System.out.println("Setup Table Columns");
        colSBD_1.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
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
    @FXML
    private void handleCallNext() {
        if (appointmentListWaitToday.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Không có bệnh nhân nào trong danh sách!");
            alert.setContentText("Vui lòng kiểm tra lại danh sách đặt lịch.");
            alert.show();
            return;
        }

        // Lấy bệnh nhân đầu tiên trong danh sách chờ
        Appointment nextAppointment = appointmentListWaitToday.remove(0);

        // Thêm vào danh sách bệnh nhân đang khám
        appointmentListProcessing.clear(); // Chỉ giữ một bệnh nhân đang khám
        appointmentListProcessing.add(nextAppointment);

        // Hiển thị thông báo bệnh nhân tiếp theo
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bệnh nhân tiếp theo");
        alert.setHeaderText("Thông tin bệnh nhân:");
        alert.setContentText(
                "Tên: " + nextAppointment.getPatient().getName() + "\n" +
                        "SBD: " + nextAppointment.getRegistrationNumber() + "\n" +
                        "SĐT: " + nextAppointment.getPatient().getPhone() + "\n" +
                        "Thời gian khám: " + nextAppointment.getAppointmentDate().toString() + "\n" +
                        "Bác sĩ: " + nextAppointment.getStaff().getName()
        );
        alert.show();

        // Cập nhật bảng
        updateTables();
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
            System.out.println("Không thể load file FXML: " + ex.getMessage());
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
            return "Cao";
        } else if ((gender == Gender.FEMALE && age >= 50) || (gender == Gender.MALE && age >= 40)) {
            return "Trung Bình";
        } else {
            return "Thấp";
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
    private void startAutoUpdate() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> loadAppointmentsToday()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

}
