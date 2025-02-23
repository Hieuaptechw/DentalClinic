package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.MedicalRecordController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.entities.RoleType;
import com.dentalclinic.entities.UserSession;
import com.dentalclinic.views.pages.form.AppointmentFormController;
import com.dentalclinic.views.pages.form.ExaminationFormController;
import com.dentalclinic.views.pages.form.PatientFormController;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.PatientRecordTableController;
import jakarta.persistence.EntityManager;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Page(name = "Patient", icon = "images/patient.png", fxml = "manage/patient.fxml")
public class PatientPage extends AbstractPage {

    @FXML private TableView<Patient> tableViewPatient;
    @FXML private TableColumn<Patient, String> nameColumn, emailColumn, phoneColumn,identityCardColumn, statusColumn, addressColumn, dobColumn;
    @FXML private TableColumn<Patient, Void> actionColumn;
    @FXML private  TextField searchField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private CheckBox checkBoxDate;
    private ObservableList<Patient> patientList = FXCollections.observableArrayList();
    private PatientController patientController;
    private MedicalRecordController medicalRecordController;

    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        patientController = new PatientController(em);
        medicalRecordController = new MedicalRecordController(em);
        setupTableColumns();
        loadPatients();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        identityCardColumn.setCellValueFactory(new PropertyValueFactory<>("identityCard"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadActionColumn();


    }
    private void loadActionColumn(){
    actionColumn.setCellFactory(param -> new TableCell<>() {
        private final Button editButton;
        private final Button deleteButton;
        private final Button viewButton;
        private final Button addButton;
        private final Button addAppointment;
        private final HBox buttonBox;
        {
            ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/edit.png")));
            editIcon.setFitHeight(22);
            editIcon.setFitWidth(22);

            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete_1.png")));
            deleteIcon.setFitHeight(22);
            deleteIcon.setFitWidth(22);

            ImageView viewIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/list.png")));
            viewIcon.setFitHeight(22);
            viewIcon.setFitWidth(22);

            ImageView addIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/add.png")));
            addIcon.setFitHeight(22);
            addIcon.setFitWidth(22);

            ImageView appointmentIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/appointment.png")));
            appointmentIcon.setFitHeight(22);
            appointmentIcon.setFitWidth(22);


            editButton = new Button("", editIcon);
            deleteButton = new Button("", deleteIcon);
            viewButton = new Button("", viewIcon);
            addButton = new Button("", addIcon);
            addAppointment = new Button("", appointmentIcon);
            editButton.getStyleClass().add("button-icon");
            deleteButton.getStyleClass().add("button-icon");
            viewButton.getStyleClass().add("button-icon");
            addButton.getStyleClass().add("button-icon");
            addAppointment.getStyleClass().add("button-icon");


            editButton.setOnAction(event -> {
                Patient patient = getTableView().getItems().get(getIndex());
                System.out.println(patient);
                handleEditPatient(patient);
            });

            deleteButton.setOnAction(event -> {
                Patient patient = getTableView().getItems().get(getIndex());
                handleDeletePatient(patient);
            });

            viewButton.setOnAction(event -> {
                Patient patient = getTableView().getItems().get(getIndex());
                List<MedicalRecord> medicalRecordList = medicalRecordController.getMedicalRecordsByPatientId(patient.getPatientId());
                handleShowPatientRecord(medicalRecordList);
            });
            addButton.setOnAction(event -> {
                Patient patient = getTableView().getItems().get(getIndex());
                handleShowExamination(patient);
            });
            addAppointment.setOnAction(event -> {
                Patient patient = getTableView().getItems().get(getIndex());
                handleShowAppointment(patient);
            });
            buttonBox = new HBox(5, editButton , viewButton,addButton,addAppointment, deleteButton);
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
                    buttonBox.getChildren().setAll(editButton , viewButton,addButton,addAppointment, deleteButton);
                } else if (role == RoleType.DOCTOR) {
                    buttonBox.getChildren().setAll(editButton, viewButton);
                } else {
                    buttonBox.getChildren().setAll(editButton , viewButton,addButton,addAppointment);
                }
                setGraphic(buttonBox);
            }
        }
    });
}
    private void loadPatients() {
        List<Patient> patients = patientController.getAllPatients();
        patientList.setAll(patients);
        tableViewPatient.setItems(patientList);
    }
    @FXML
    private void handleFilter() {

        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            tableViewPatient.setItems(FXCollections.observableArrayList(patientList));
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
        List<Patient> filteredList = patientList.stream()
                .filter(patient -> {
                    LocalDateTime createdAt = patient.getCreatedAt();
                    return !createdAt.toLocalDate().isBefore(fromDate) && !createdAt.toLocalDate().isAfter(toDate);
                })
                .collect(Collectors.toList());

        tableViewPatient.setItems(FXCollections.observableArrayList(filteredList));
        loadActionColumn();
        if (filteredList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No patients found within this time period!", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Found " + filteredList.size() + " patients!", ButtonType.OK);
            alert.showAndWait();
        }

    }

    private void handleShowExamination(Patient patient){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/examinationform.fxml"));
            Parent root = loader.load();
            ExaminationFormController controller = loader.getController();
            controller.setPatientData(patient);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Examination");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void handleShowAppointment(Patient patient){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/appointmentform.fxml"));
            Parent root = loader.load();
            AppointmentFormController controller = loader.getController();
            controller.setPatient(patient);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Appointment");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void handleShowPatientRecord(List <MedicalRecord> medicalRecordList) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/medicalrecordtable.fxml"));
            VBox root =(VBox) loader.load();
            PatientRecordTableController patientRecordTableController = loader.getController();
            patientRecordTableController.loadData(medicalRecordList);
            Stage stage = new Stage();
            stage.setTitle("List Patient Records");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadPatients();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }




    @FXML
    private void handleSearch(String keyword) {
        String trimmedKeyword = keyword.toLowerCase().trim();

        if (trimmedKeyword.isEmpty()) {
            tableViewPatient.setItems(patientList);
        } else {
            List<Patient> filteredList = patientList.stream()
                    .filter(patient -> matchesSearch(patient, trimmedKeyword))
                    .toList();

            tableViewPatient.setItems(FXCollections.observableArrayList(filteredList));
        }
        loadActionColumn();
    }

    private boolean matchesSearch(Patient patient, String keyword) {
        String[] keywords = keyword.split("\\s+");

        String combinedData = (patient.getIdentityCard() + " " +
                patient.getEmail()).toLowerCase();

        return Arrays.stream(keywords).allMatch(combinedData::contains);
    }


    private void handleDeletePatient(Patient patient) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this patient?");
        alert.setContentText("Name: " + patient.getName());

        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                patientController.deletePatient(patient.getPatientId());
                loadPatients();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Patient deleted successfully!", ButtonType.OK);
                alert1.showAndWait();
            }
        });
    }

    @FXML
    private void handleAddPatient() {
        System.out.println("Đang gọi handleAddPatient()...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/patientform.fxml"));
            AnchorPane root = loader.load();
            PatientFormController patientFormController = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Add New Patient");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadPatients();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    private void handleEditPatient(Patient patient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/patientform.fxml"));
            AnchorPane root = loader.load();
            PatientFormController patientFormController = loader.getController();
            patientFormController.setPatient(patient);
            Stage stage = new Stage();
            stage.setTitle("Edit New Patient");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadPatients();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
