package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.WorkSheduleController;
import com.dentalclinic.entities.WorkSchedule;
import jakarta.persistence.EntityManager;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class WorkScheduleController {

    private LocalDate date;
    private ObservableList<WorkSchedule> schedules;

    @FXML
    private TableView<WorkSchedule> workScheduleTable;
    @FXML
    private TableColumn<WorkSchedule, String> name;
    @FXML
    private TableColumn<WorkSchedule, String> role;
    @FXML
    private TableColumn<WorkSchedule, String> startTime;
    @FXML
    private TableColumn<WorkSchedule, String> endTime;
    @FXML
    private TableColumn<WorkSchedule, Void> action;

    private WorkSheduleController workSheduleController;

    @FXML
    private void initialize() {
        name.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStaff().getName()));
        role.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStaff().getRole().toString()));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        EntityManager em = DatabaseController.getEntityManager();
        workSheduleController = new WorkSheduleController(em);

        loadActionColumn();
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSchedules(List<WorkSchedule> scheduleList) {
        this.schedules = FXCollections.observableArrayList(scheduleList);
        workScheduleTable.setItems(schedules);
    }

    private void loadActionColumn() {
        action.setCellFactory(param -> new TableCell<>() {
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
                    WorkSchedule schedule = getTableView().getItems().get(getIndex());
                    handleEditSchedule(schedule);
                });

                deleteButton.setOnAction(event -> {
                    WorkSchedule schedule = getTableView().getItems().get(getIndex());
                    handleDeleteSchedule(schedule);
                });

                viewButton.setOnAction(event -> {
                    WorkSchedule schedule = getTableView().getItems().get(getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editButton, viewButton, deleteButton));
                }
            }
        });
    }
private void handleDeleteSchedule(WorkSchedule schedule) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Deletion");
    alert.setHeaderText("Are you sure you want to delete this work schedule?");
    alert.setContentText(schedule.getStaff().getName()+"-Start: "+schedule.getStartTime()+"-End: "+schedule.getEndTime());

    ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
    ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(buttonYes, buttonNo);

    alert.showAndWait().ifPresent(response -> {
        if (response == buttonYes) {
            workSheduleController.deleteWorkSchedule(schedule.getScheduleId());
            reloadTableData();
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Work Schedule deleted successfully!", ButtonType.OK);
            alert1.showAndWait();
        }
    });
}
    private void handleEditSchedule(WorkSchedule schedule) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/worksheduleform.fxml"));
            VBox root = (VBox) loader.load();
            WorkScheduleFormController workScheduleFormController = loader.getController();
            workScheduleFormController.setWorkSchedule(schedule);
            Stage stage = new Stage();
            stage.setTitle("Edit Work Schedule");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            reloadTableData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadTableData() {
        List<WorkSchedule> updatedSchedules = workSheduleController.getAllWorkSchedulesToday();
        schedules.setAll(updatedSchedules);
        workScheduleTable.setItems(schedules);
    }
}
