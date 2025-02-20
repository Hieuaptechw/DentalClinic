package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.MedicalRecordController;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.PatientRecordFormController;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Page(name="Bệnh án", icon="images/records.png", fxml="manage/patientrecord.fxml")
public class PatientRecordPage extends AbstractPage {
    @FXML
    private TableView<MedicalRecord> recordTable;

    @FXML
    private TableColumn<MedicalRecord, String> nameColumn, diagnosisColumn, treatmentColumn;

    @FXML
    private TableColumn<MedicalRecord, LocalDate> dateColumn;

    @FXML
    private TableColumn<MedicalRecord, Void> actionColumn;

    @FXML
    private TextField searchField;

    private ObservableList<MedicalRecord> medicalRecordObservableList = FXCollections.observableArrayList();

    private MedicalRecordController patientRecordController;

    public PatientRecordPage(){
        EntityManager em = DatabaseController.getEntityManager();
        this.patientRecordController = new MedicalRecordController(em);
    }

    @FXML
    private void initialize(){
        setupTableColumn();
        loadPatientRecord();
        searchField.textProperty().addListener((observable, oldValue, newValue)-> handleSearch(newValue));
    }

    public void setupTableColumn(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        treatmentColumn.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        loadActionColumn();
    }

    public void loadActionColumn(){
        actionColumn.setCellFactory(KParameter -> new TableCell<MedicalRecord, Void>(){
            private final Button editButton;
            private final Button deleteButton;
            {
                // Tạo icon sửa
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/edit.png")));
                editIcon.setFitWidth(22);
                editIcon.setFitHeight(22);
                editButton = new Button("", editIcon);
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                editButton.setOnAction(event -> {
                    MedicalRecord medicalRecord = getTableView().getItems().get(getIndex());
                    handleEditPatientRecord(medicalRecord);
                });

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete_1.png")));
                deleteIcon.setFitWidth(22);
                deleteIcon.setFitHeight(22);
                deleteButton = new Button("", deleteIcon);
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                deleteButton.setOnAction(event -> {
                    MedicalRecord medicalRecord = getTableView().getItems().get(getIndex());
                    if (medicalRecord != null) {
                        handleDelete(medicalRecord);
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

    

    public void loadPatientRecord(){
        List<MedicalRecord> medicalRecords = patientRecordController.getAllPatientRecord();
        medicalRecordObservableList.setAll(medicalRecords);
        recordTable.setItems(medicalRecordObservableList);
    }

    public void handleDelete(MedicalRecord medicalRecord){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Xóa bệnh án");
        alert.setContentText("Bạn có chắc chắn muốn xóa : " + medicalRecord.getPatient() + "?");
        alert.showAndWait().ifPresent(response -> {
            if(response == ButtonType.OK){
                patientRecordController.handleDeletePatientRecord(medicalRecord.getRecordId());
                medicalRecordObservableList.remove(medicalRecord);
            }
        });
    }

    public void handleEditPatientRecord(MedicalRecord medicalRecord){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/editPatientRecord.fxml"));
            Parent root = loader.load();
            PatientRecordFormController editPatientRecordPage = loader.getController();
            editPatientRecordPage.setPatientRecordData(medicalRecord);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sửa bệnh án");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadPatientRecord();
            System.out.println("Đã load");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAddPatientRecord(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/addPatientRecord.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thêm bệnh án");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadPatientRecord();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearch(String keyword){
        String key = keyword.toLowerCase().trim();
        if(key.isEmpty()){
            recordTable.setItems(medicalRecordObservableList);
        }else {
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





}