package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.CandidateNumberController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.ExaminationRecordController;
import com.dentalclinic.entities.*;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.CandidatenumberFormController;
import com.dentalclinic.views.pages.form.ExaminationFormController;
import jakarta.persistence.EntityManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Page(name="Khám bệnh", icon="images/waiting-room.png", fxml="manage/examination.fxml")
public class ExaminationPage extends AbstractPage {
    @FXML
    private TableView<ExaminationRecord> tableExamination;

    @FXML
    private TableColumn<ExaminationRecord, String> nameColumn, reasonColumn, symptomsColumn, doctorColumn, roomColumn, dateColumn;

    @FXML
    private TableColumn<ExaminationRecord, Void> actionColumn;

    private ExaminationRecordController examinationRecordController;
    private ObservableList<ExaminationRecord> examinationPageObservableList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        examinationRecordController = new ExaminationRecordController(em);
        setupTableView();
        loadData();
    }

    private void loadData(){
        List<ExaminationRecord> examinationRecordList = examinationRecordController.getAllExaminationList();
        examinationPageObservableList.setAll(examinationRecordList);
        tableExamination.setItems(examinationPageObservableList);
    }

    private void setupTableView() {
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPatient().getName()));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        symptomsColumn.setCellValueFactory(new PropertyValueFactory<>("symptoms"));
        doctorColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStaff().getName()));
        roomColumn.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getRoom().getRoomNumber()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfVisit"));
        loadActionColumn();
    }


    private void loadActionColumn(){
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton;
            private final Button deleteButton;
            private final Button viewButton;
            private final Button addButton;


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

                ImageView addIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/add.png")));
                addIcon.setFitHeight(22);
                addIcon.setFitWidth(22);



                editButton = new Button("", editIcon);
                deleteButton = new Button("", deleteIcon);
                viewButton = new Button("", viewIcon);
                addButton = new Button("", addIcon);
                editButton.getStyleClass().add("button-icon");
                deleteButton.getStyleClass().add("button-icon");
                viewButton.getStyleClass().add("button-icon");
                addButton.getStyleClass().add("button-icon");

                editButton.setOnAction(event -> {
                    ExaminationRecord examinationRecord = getTableView().getItems().get(getIndex());

                });

                deleteButton.setOnAction(event -> {
                    ExaminationRecord examinationRecord = getTableView().getItems().get(getIndex());

                });

                viewButton.setOnAction(event -> {
                    ExaminationRecord examinationRecord = getTableView().getItems().get(getIndex());
                    System.out.println(examinationRecord);
                    handleShowExamination(examinationRecord);

                });
                addButton.setOnAction(event -> {
                    ExaminationRecord examinationRecord = getTableView().getItems().get(getIndex());
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5 , viewButton,deleteButton));
                }
            }

        });
    }



    private void handleShowExamination(ExaminationRecord examinationRecord){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/examinationView.fxml"));
            Parent root = loader.load();
            ExaminationFormController examinationFormController = loader.getController();
            examinationFormController.setExamination(examinationRecord);
            Stage stage = new Stage();
            stage.setTitle("Information Examinations");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadData();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}