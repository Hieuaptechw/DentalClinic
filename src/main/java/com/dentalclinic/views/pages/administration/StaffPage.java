package com.dentalclinic.views.pages.administration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.StaffController;
import com.dentalclinic.entities.*;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.PatientFormController;

import com.dentalclinic.views.pages.form.StaffFormController;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Page(name="Nhận sự", icon="images/staff.png", fxml="administration/staff.fxml")
public class StaffPage extends AbstractPage {

    @FXML private TableView<Staff> tableViewPatient;
    @FXML private TableColumn<Staff, String> nameColumn;
    @FXML private TableColumn<Staff, String> emailColumn;
    @FXML private TableColumn<Staff, String> phoneColumn;
    @FXML private TableColumn<Staff, String> roleColumn;
    @FXML private TableColumn<Staff, String> specialtyColumn;
    @FXML private TableColumn<Staff, Void> actionColumn;
    @FXML private TextField searchField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;


    private ObservableList<Staff> staffList = FXCollections.observableArrayList();
    private StaffController staffController;

    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        staffController = new StaffController(em);
        setupTableColumns();
        loadStaffs();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        loadActionColumn();
    }

    private void loadActionColumn(){
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton;
            private final Button deleteButton;
            private final Button addButton;
            private final HBox buttonBox;


            {
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/edit.png")));
                editIcon.setFitHeight(22);
                editIcon.setFitWidth(22);

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete_1.png")));
                deleteIcon.setFitHeight(22);
                deleteIcon.setFitWidth(22);


                ImageView addIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/add.png")));
                addIcon.setFitHeight(22);
                addIcon.setFitWidth(22);




                editButton = new Button("", editIcon);
                deleteButton = new Button("", deleteIcon);
                addButton = new Button("", addIcon);
                editButton.getStyleClass().add("button-icon");
                deleteButton.getStyleClass().add("button-icon");
                addButton.getStyleClass().add("button-icon");

                editButton.setOnAction(event -> {
                    Staff staff = getTableRow().getItem();
                    if (staff != null) {
                        handleEditStaff(staff);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Staff staff = getTableRow().getItem();
                    if (staff != null) {
                        handleDeleteStaff(staff);
                    }

                });

                buttonBox = new HBox(5, editButton,deleteButton);
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
                        buttonBox.getChildren().setAll(editButton, deleteButton);
                    }
                    setGraphic(buttonBox);
                }
            }

        });
    }
    private void loadStaffs() {
        List<Staff> staff = staffController.getAllStaff();
        staffList.setAll(staff);
        tableViewPatient.setItems(staffList);
    }


    @FXML
    private void handleSearch(String keyword) {
        String trimmedKeyword = keyword.toLowerCase().trim();

        if (trimmedKeyword.isEmpty()) {
            tableViewPatient.setItems(staffList);
        } else {
            List<Staff> filteredList = staffList.stream()
                    .filter(patient -> matchesSearch(patient, trimmedKeyword))
                    .toList();

            tableViewPatient.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    private boolean matchesSearch(Staff staff, String keyword) {
        String[] keywords = keyword.split("\\s+");

        String combinedData = (staff.getName() + " " +
                staff.getEmail() + " " +
                staff.getPhone() + " " +
                staff.getRole() + " " +
                staff.getSpecialty()).toLowerCase();

        return Arrays.stream(keywords).allMatch(combinedData::contains);
    }


    private void handleDeleteStaff(Staff staff) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this staff member?");
        alert.setContentText("Name: " + staff.getName());

        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                staffController.deleteStaff(staff.getStaffId());
                loadStaffs();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Staff deleted successfully!", ButtonType.OK);
                alert1.showAndWait();
            }
        });
    }

    @FXML
    private void handleAddStaff() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/staff-form.fxml"));
            Pane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Staff");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadStaffs();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Không thể load file FXML: " + ex.getMessage());
        }
    }
    @FXML
    private void handleEditStaff(Staff staff) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/staff-form.fxml"));

            Pane root = loader.load();
            StaffFormController staffFormController = loader.getController();
            staffFormController.setStaff(staff);
            Stage stage = new Stage();
            stage.setTitle("Edit Staff");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadStaffs();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Không thể load file FXML: " + ex.getMessage());
        }
    }
}
