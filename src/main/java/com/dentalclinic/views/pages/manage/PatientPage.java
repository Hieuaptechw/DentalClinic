package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.views.pages.form.PatientFormController;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Page(name = "Bệnh nhân", icon = "images/patient.png", fxml = "manage/patient.fxml")
public class PatientPage extends AbstractPage {

    @FXML private TableView<Patient> tableViewPatient;
    @FXML private TableColumn<Patient, String> nameColumn;
    @FXML private TableColumn<Patient, String> emailColumn;
    @FXML private TableColumn<Patient, String> phoneColumn;
    @FXML private TableColumn<Patient, String> addressColumn;
    @FXML private TableColumn<Patient, String> dobColumn;
    @FXML private TableColumn<Patient, String> genderColumn;
    @FXML private TableColumn<Patient, String> dateRColumn;
    @FXML private TableColumn<Patient, Void> actionColumn;
    @FXML private  TextField searchField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;


    private ObservableList<Patient> patientList = FXCollections.observableArrayList();
    private PatientController patientController;

    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        patientController = new PatientController(em);
        setupTableColumns();
        loadPatients();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        dateRColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        loadActionColumn();


    }
    private void loadActionColumn(){
    actionColumn.setCellFactory(param -> new TableCell<>() {
        private final Button editButton;
        private final Button deleteButton;
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


            editButton = new Button("", editIcon);
            deleteButton = new Button("", deleteIcon);
            viewButton = new Button("", viewIcon);
            editButton.getStyleClass().add("button-icon");
            deleteButton.getStyleClass().add("button-icon");
            viewButton.getStyleClass().add("button-icon");

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
                // handleViewPatient(patient);
            });
        }


        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(new HBox(5, editButton , viewButton,deleteButton));
            }
        }

    });
}
    private void loadPatients() {
        List<Patient> patients = patientController.getAllPatients();
        System.out.println("Số lượng bệnh nhân: " + patients.size());
//        patients.forEach(System.out::println);
        patientList.setAll(patients);
        tableViewPatient.setItems(patientList);
    }
    @FXML
    private void handleFilter() {
        System.out.println("Đang lọc...");

        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            tableViewPatient.setItems(FXCollections.observableArrayList(patientList));
            loadActionColumn();
            return;
        }

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        if (toDate.isBefore(fromDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu!", ButtonType.OK);
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
            Alert alert = new Alert(Alert.AlertType.WARNING, "Không tìm thấy bệnh nhân nào trong khoảng thời gian này!", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Tìm thấy " + filteredList.size() + " bệnh nhân!", ButtonType.OK);
            alert.showAndWait();
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

        String combinedData = (patient.getName() + " " +
                patient.getEmail() + " " +
                patient.getPhone() + " " +
                patient.getAddress()).toLowerCase();

        return Arrays.stream(keywords).allMatch(combinedData::contains);
    }


    private void handleDeletePatient(Patient patient) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa bệnh nhân này?");
        alert.setContentText("Tên: " + patient.getName());

        ButtonType buttonYes = new ButtonType("Có", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("Không", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                patientController.deletePatient(patient.getPatientId());
                loadPatients();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Xóa bệnh nhân thành công!", ButtonType.OK);
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
            System.out.println("Không thể load file FXML: " + ex.getMessage());
        }
    }
    @FXML
    private void handleEditPatient(Patient patient) {
        System.out.println("Đang gọi handleAddPatient()...");
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
            System.out.println("Không thể load file FXML: " + ex.getMessage());
        }
    }

}
