package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientRecordController;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.EditInventoryPage;
import com.dentalclinic.views.pages.form.EditPatientRecordPage;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
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
    private TableColumn<MedicalRecord, String> nameColumn;

    @FXML
    private TableColumn<MedicalRecord, String> diagnosisColumn;

    @FXML
    private TableColumn<MedicalRecord, String> treatmentColumn;

    @FXML
    private TableColumn<MedicalRecord, LocalDate> dateColumn;

    @FXML
    private TableColumn<MedicalRecord, Void> actionColumn;

    private ObservableList<MedicalRecord> medicalRecordObservableList = FXCollections.observableArrayList();

    private PatientRecordController patientRecordController;

    public PatientRecordPage(){
        EntityManager em = DatabaseController.getEntityManager();
        this.patientRecordController = new PatientRecordController(em);
    }

    @FXML
    private void initialize(){
        setupTableColumn();
        loadPatientRecord();
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
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/pen.png")));
                editIcon.setFitWidth(30);
                editIcon.setFitHeight(30);
                editButton = new Button("", editIcon);
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                editButton.setOnAction(event -> {
                    MedicalRecord medicalRecord = getTableView().getItems().get(getIndex());
                    handleEditPatientRecord(medicalRecord);
                });

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete.png")));
                deleteIcon.setFitWidth(30);
                deleteIcon.setFitHeight(30);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/EditPatientRecord.fxml"));
            Parent root = loader.load();
            EditPatientRecordPage editPatientRecordPage = loader.getController();
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






}
