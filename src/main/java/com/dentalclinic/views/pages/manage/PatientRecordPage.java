package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientRecordController;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

@Page(name="Bệnh án", icon="images/records.png", fxml="manage/patientrecord.fxml")
public class PatientRecordPage extends AbstractPage {
    @FXML
    private TableView<MedicalRecord> recordTable;

    @FXML
    private TableColumn<MedicalRecord, Long> idColumn;

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
        loadPatientRecord();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        treatmentColumn.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        recordTable.setItems(medicalRecordObservableList);
    }

    public void loadPatientRecord(){
        List<MedicalRecord> medicalRecords = patientRecordController.getAllPatientRecord();
        medicalRecordObservableList.addAll(medicalRecords);
    }








}
