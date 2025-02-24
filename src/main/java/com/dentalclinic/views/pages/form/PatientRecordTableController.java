package com.dentalclinic.views.pages.form;

import com.dentalclinic.entities.MedicalRecord;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PatientRecordTableController {
    @FXML
    private TableView<MedicalRecord> medicalRecordTableView;
    @FXML
    private TableColumn<MedicalRecord, String> dateColumn;
    @FXML
    private TableColumn<MedicalRecord, String> doctorNameColumn;
    @FXML
    private TableColumn<MedicalRecord, String> diagnosisColumn;
    @FXML
    private TableColumn<MedicalRecord, String> followUpDateColumn;
    @FXML
    private TableColumn<MedicalRecord, String> notesColumn;
    @FXML
    private TableColumn<MedicalRecord, String> symptomTypeColumn;
    @FXML
    private TableColumn<MedicalRecord, String> treatmentTypeColumn;
    @FXML
    private TableColumn<MedicalRecord, Void> actionColumn;
    @FXML
    private ScrollPane scrollPane;
    private ObservableList<MedicalRecord> medicalRecordsObservableList;

    @FXML
    private void initialize() {
        medicalRecordsObservableList = FXCollections.observableArrayList();
        setupTableColumns();
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(c ->
                new SimpleStringProperty(formatValue(c.getValue().getDateOfVisit()))
        );

        doctorNameColumn.setCellValueFactory(c ->
                new SimpleStringProperty(formatValue(c.getValue().getDoctor() != null ? c.getValue().getDoctor().getName() : null))
        );

        diagnosisColumn.setCellValueFactory(c ->
                new SimpleStringProperty(formatValue(c.getValue().getDiagnosis()))
        );

        followUpDateColumn.setCellValueFactory(c ->
                new SimpleStringProperty(formatValue(c.getValue().getFollowUpDate()))
        );

        notesColumn.setCellValueFactory(c ->
                new SimpleStringProperty(formatValue(c.getValue().getNotes()))
        );

        symptomTypeColumn.setCellValueFactory(c ->
                new SimpleStringProperty(formatValue(c.getValue().getSymptoms()))
        );

        treatmentTypeColumn.setCellValueFactory(c ->
                new SimpleStringProperty(formatValue(c.getValue().getTreatment()))
        );

        loadActionColumn();
    }

    public void loadData(List<MedicalRecord> medicalRecords) {
        this.medicalRecordsObservableList.setAll(medicalRecords);
        medicalRecordTableView.setItems(medicalRecordsObservableList);
    }

    private void loadActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton;
            private final Button printButton;

            {
                ImageView viewIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/export.png")));

                ImageView printIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/printer.png")));
                viewIcon.setFitHeight(22);
                viewIcon.setFitWidth(22);
                printIcon.setFitHeight(22);
                printIcon.setFitWidth(22);
                viewButton = new Button("", viewIcon);
                printButton = new Button("", printIcon);
                viewButton.getStyleClass().add("button-icon");
                printButton.getStyleClass().add("button-icon");

                viewButton.setOnAction(event -> {
                    MedicalRecord selectedRecord =  getTableView().getItems().get(getIndex());
                    showMedicalRecordDetails(selectedRecord);
                });
                printButton.setOnAction(event -> {
                    MedicalRecord selectedRecord =  getTableView().getItems().get(getIndex());
                    printMedicalRecord(selectedRecord);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, viewButton,printButton));
            }
        });
    }

    private void showMedicalRecordDetails(MedicalRecord record) {
        System.out.println(record);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/medicalrecordform.fxml"));
            VBox root = loader.load();
            PatientRecordFormController patientRecordFormController = loader.getController();
            patientRecordFormController.setPatientRecordInformation(record);
            ScrollPane newScrollPane = new ScrollPane(root);
            root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            newScrollPane.setFitToWidth(false);
            newScrollPane.setFitToHeight(false);
            Stage stage = new Stage();
            stage.setTitle("List Patient Records");
            stage.setScene(new Scene(newScrollPane, 600, 700));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void printMedicalRecord(MedicalRecord record) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/medicalrecordform.fxml"));
            VBox originalVBox = loader.load();
            PatientRecordFormController patientRecordFormController = loader.getController();
            patientRecordFormController.setPatientRecordInformation(record);
            VBox cloneVBox = new VBox();
            cloneVBox.getChildren().setAll(originalVBox.getChildren());
            cloneVBox.setStyle(originalVBox.getStyle());
            cloneVBox.setAlignment(originalVBox.getAlignment());
            cloneVBox.setSpacing(originalVBox.getSpacing());
            cloneVBox.setPadding(originalVBox.getPadding());
            cloneVBox.setPrefWidth(originalVBox.getPrefWidth());
            cloneVBox.setPrefHeight(originalVBox.getPrefHeight());
            cloneVBox.getStylesheets().addAll(originalVBox.getStylesheets());
            cloneVBox.getStyleClass().addAll(originalVBox.getStyleClass());
            printNode(cloneVBox);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void printNode(Node node) {

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null || !job.showPrintDialog(null)) {
            return;
        }
        PageLayout pageLayout = job.getPrinter().getDefaultPageLayout();
        double pageWidth = pageLayout.getPrintableWidth();
        double pageHeight = pageLayout.getPrintableHeight();
        double contentWidth = node.prefWidth(-1);
        double contentHeight = node.prefHeight(-1);
        double scaleX = pageWidth / contentWidth;
        double scaleY = pageHeight / contentHeight;
        double scale = Math.min(scaleX, scaleY);
        Group group = new Group(node);
        group.setScaleX(scale);
        group.setScaleY(scale);
        double centerX = (pageWidth - (contentWidth * scale)) / 2 - 75;
        double centerY = (pageHeight - (contentHeight * scale)) / 2-130;
        group.setTranslateX(centerX);
        group.setTranslateY(centerY);
        boolean success = job.printPage(group);
        if (success) {
            job.endJob();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Print successfully!", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Print error!", ButtonType.OK);
            alert.showAndWait();
        }
    }



    private String formatValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "N/A";
        }
        return value;
    }

    private String formatValue(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        return date.toString();
    }

}
