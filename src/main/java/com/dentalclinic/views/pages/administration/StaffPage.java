package com.dentalclinic.views.pages.administration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.controllers.StaffController;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.PatientFormController;

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
        actionColumn.setCellFactory(KParameter -> new TableCell<Staff, Void>() {
            private final Button editButton;
            private final Button deleteButton;

            {
                // Tạo icon sửa
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/pen.png")));
                editIcon.setFitWidth(20);
                editIcon.setFitHeight(20);
                editButton = new Button("", editIcon); // Nút không có text, chỉ có icon
                editButton.setStyle("-fx-cursor: hand;");

                editButton.setOnAction(event -> {
                    Staff staff = getTableRow().getItem();
                    if (staff != null) {
                        // loadViewEditProduct(inventory);
                    }
                });

                // Tạo icon xóa
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete.png")));
                deleteIcon.setFitWidth(20);
                deleteIcon.setFitHeight(20);
                deleteButton = new Button("", deleteIcon);
                deleteButton.setStyle("-fx-cursor: hand;");

                deleteButton.setOnAction(event -> {
                    Staff staff = getTableRow().getItem();
                    if (staff != null) {
                        // handleDelete(inventory);
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

    private void loadStaffs() {
        List<Staff> patients = staffController.getAllStaff();
        System.out.println("Số lượng bệnh nhân: " + patients.size());
//        patients.forEach(System.out::println);
        staffList.setAll(patients);
        tableViewPatient.setItems(staffList);
    }

    @FXML
    private void handleFilter() {
        System.out.println("Đang lọc...");

        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            tableViewPatient.setItems(FXCollections.observableArrayList(staffList));
            return;
        }

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        if (toDate.isBefore(fromDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        List<Staff> filteredList = staffList.stream()
                .filter(patient -> {
                    LocalDateTime createdAt = patient.getCreatedAt();
                    return !createdAt.toLocalDate().isBefore(fromDate) && !createdAt.toLocalDate().isAfter(toDate);
                })
                .collect(Collectors.toList());

        tableViewPatient.setItems(FXCollections.observableArrayList(filteredList));

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
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa bệnh nhân này?");
        alert.setContentText("Tên: " + staff.getName());

        ButtonType buttonYes = new ButtonType("Có", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("Không", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                staffController.deleteStaff(staff.getStaffId());
                loadStaffs();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Xóa bệnh nhân thành công!", ButtonType.OK);
                alert1.showAndWait();
            }
        });
    }

    @FXML
    private void handleAddPatient() {
        System.out.println("Đang gọi handleAddPatient()...");
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/staffform.fxml"));

            AnchorPane root = loader.load();
            PatientFormController patientFormController = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Add New Patient");
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
    private void handleEditPatient(Patient patient) {
        System.out.println("Đang gọi handleAddPatient()...");
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/staffform.fxml"));

            AnchorPane root = loader.load();
            PatientFormController patientFormController = loader.getController();
            patientFormController.setPatient(patient);
            Stage stage = new Stage();
            stage.setTitle("Edit New Patient");
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
