package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.ExaminationRecordController;
import com.dentalclinic.entities.*;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.ExaminationFormController;
import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.*;

@Page(name="Examination", icon="images/waiting-room.png", fxml="manage/examination.fxml")
public class ExaminationPage extends AbstractPage {
    @FXML
    private TableView<ExaminationRecord> tableExamination;

    @FXML
    private TableColumn<ExaminationRecord, String> nameColumn, reasonColumn, symptomsColumn, doctorColumn, roomColumn, dateColumn,statusColumn;
    @FXML
    private TableColumn<ExaminationRecord, Void> actionColumn;

    private ExaminationRecordController examinationRecordController;
    private ObservableList<ExaminationRecord> examinationPageObservableList = FXCollections.observableArrayList();
    private RoleType roleType;
    private Staff user;
    @FXML
    private void initialize() {
        user = UserSession.getCurrentUser();
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        examinationRecordController = new ExaminationRecordController(em);
        setupTableView();
        loadData();
    }

    private void loadData() {
        List<ExaminationRecord> examinationRecordList =
                (user.getRole() == RoleType.DOCTOR
                        ? examinationRecordController.getAllExaminationListOfDoctor(user.getStaffId())
                        : examinationRecordController.getAllExaminationList()
                )
                        .stream()
                        .filter(e -> e.getStatus() == ExaminationStatus.ONGOING)
                        .sorted(Comparator.comparing(ExaminationRecord::getDateOfVisit))
                        .toList();

        System.out.println("Số lượng hồ sơ: " + examinationRecordList.size());

        examinationPageObservableList.setAll(examinationRecordList);
        tableExamination.setItems(examinationPageObservableList);
    }


    private void setupTableView() {
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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
            private final Button examinationButton;
            private final Button addButton;
            private final HBox buttonBox;


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
                ImageView examinationIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/examination.png")));
                examinationIcon.setFitHeight(22);
                examinationIcon.setFitWidth(22);



                editButton = new Button("", editIcon);
                deleteButton = new Button("", deleteIcon);
                viewButton = new Button("", viewIcon);
                addButton = new Button("", addIcon);
                examinationButton = new Button("", examinationIcon);
                editButton.getStyleClass().add("button-icon");
                deleteButton.getStyleClass().add("button-icon");
                viewButton.getStyleClass().add("button-icon");
                addButton.getStyleClass().add("button-icon");
                examinationButton.getStyleClass().add("button-icon");

                editButton.setOnAction(event -> {
                    ExaminationRecord examinationRecord = getTableView().getItems().get(getIndex());
                    handleEditExamination(examinationRecord);
                });

                deleteButton.setOnAction(event -> {
                    ExaminationRecord examinationRecord = getTableView().getItems().get(getIndex());

                });

                viewButton.setOnAction(event -> {
                    ExaminationRecord examinationRecord = getTableView().getItems().get(getIndex());
                    handleShowExamination(examinationRecord);

                });
                examinationButton.setOnAction(event -> {
                    ExaminationRecord examinationRecord = getTableView().getItems().get(getIndex());
                    handleExamination(examinationRecord);
                });
                buttonBox = new HBox(5, editButton, viewButton, examinationButton,deleteButton);
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
                        buttonBox.getChildren().setAll(editButton, viewButton, deleteButton);
                    } else if (role == RoleType.DOCTOR) {
                        buttonBox.getChildren().setAll(examinationButton,editButton, viewButton);
                    } else {
                        buttonBox.getChildren().setAll(viewButton);
                    }
                    setGraphic(buttonBox);
                }
            }

        });
    }

        private void handleExamination(ExaminationRecord examinationRecord){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/examinationformresult.fxml"));
                ScrollPane root = (ScrollPane) loader.load();
                ExaminationFormController controller = loader.getController();
                boolean upDate = true;
                controller.setExamination(examinationRecord,upDate);
                Stage stage = new Stage();
                stage.setTitle("Edit");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                loadData();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    private void handleEditExamination(ExaminationRecord examinationRecord){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/examinationform.fxml"));
            Parent root = loader.load();
            boolean upDate = true;
            ExaminationFormController controller = loader.getController();
            controller.setExamination(examinationRecord,upDate);
            Stage stage = new Stage();
            stage.setTitle("Edit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadData();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private void handleShowExamination(ExaminationRecord examinationRecord){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/examinationform.fxml"));
            Parent root = loader.load();
            ExaminationFormController examinationFormController = loader.getController();
            examinationFormController.setExaminationView(examinationRecord);
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