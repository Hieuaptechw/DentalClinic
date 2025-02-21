package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.MedicalRecordController;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.PatientRecordFormController;
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
import java.time.LocalDate;
import java.util.List;

@Page(name = "Bệnh án", icon = "images/records.png", fxml = "manage/patientrecord.fxml")
public class PatientRecordPage extends AbstractPage {
    @FXML private TableView<MedicalRecord> recordTable;
    @FXML private TableColumn<MedicalRecord, String> nameColumn, diagnosisColumn, treatmentColumn;
    @FXML private TableColumn<MedicalRecord, LocalDate> dateColumn;
    @FXML private TableColumn<MedicalRecord, Void> actionColumn;
    @FXML private TextField searchField;

    private final ObservableList<MedicalRecord> medicalRecordObservableList = FXCollections.observableArrayList();

    private final MedicalRecordController patientRecordController;

    public PatientRecordPage() {
        this.patientRecordController = new MedicalRecordController(DatabaseController.getEntityManager());
    }

    @FXML
    private void initialize() {
        setupTableColumn();
        loadPatientRecord();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    public void setupTableColumn() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        treatmentColumn.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        actionColumn.setCellFactory(col -> createActionCell());
    }

    private TableCell<MedicalRecord, Void> createActionCell() {
        return new TableCell<>() {
            private final Button editButton = createButton("/com/dentalclinic/images/edit.png", event -> handleEditPatientRecord(getTableView().getItems().get(getIndex())));
            private final Button deleteButton = createButton("/com/dentalclinic/images/delete_1.png", event -> handleDelete(getTableView().getItems().get(getIndex())));
            {
                HBox box = new HBox(10, editButton, deleteButton);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : getGraphic());
            }
        };
    }

    public Button createButton(String imagePath, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        icon.setFitWidth(22);
        icon.setFitHeight(22);
        Button button = new Button("", icon);
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        button.setOnAction(action);
        return button;
    }

    public void loadPatientRecord() {
        List<MedicalRecord> medicalRecords = patientRecordController.getAllPatientRecord();
        medicalRecordObservableList.setAll(medicalRecords);
        recordTable.setItems(medicalRecordObservableList);
    }

    private void handleDelete(MedicalRecord medicalRecord) {
        if (showAlert("Confirmation", "Delete Medical Record", "Are you sure you want to delete: " + medicalRecord.getPatient() + "?", Alert.AlertType.CONFIRMATION)) {
            patientRecordController.handleDeletePatientRecord(medicalRecord.getRecordId());
            medicalRecordObservableList.remove(medicalRecord);
        }
    }

    public void handleEditPatientRecord(MedicalRecord medicalRecord) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/editPatientRecord.fxml"));
            Parent root = loader.load();
            PatientRecordFormController editPatientRecordPage = loader.getController();
            editPatientRecordPage.setPatientRecordData(medicalRecord);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Medical Record");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadPatientRecord();
            System.out.println("Loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAddPatientRecord() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/addPatientRecord.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Medical Record");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadPatientRecord();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearch(String keyword) {
        String key = keyword.toLowerCase().trim();
        if (key.isEmpty()) {
            recordTable.setItems(medicalRecordObservableList);
        } else {
            List<MedicalRecord> filtered = medicalRecordObservableList.stream()
                    .filter(record -> matchesSearch(record, key))
                    .toList();
            recordTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    private boolean matchesSearch(MedicalRecord medicalRecord, String keyword) {
        String fullName = medicalRecord.getPatient().getName().toLowerCase();
        String[] nameParts = fullName.split("\\s+");

        String[] keywords = keyword.toLowerCase().split("\\s+");

        int index = 0;
        for (String key : keywords) {
            boolean found = false;
            while (index < nameParts.length) {
                if (nameParts[index].startsWith(key)) {
                    found = true;
                    index++;
                    break;
                }
                index++;
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private boolean showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

}