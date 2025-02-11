package com.dentalclinic.views.pages.manage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.FinancialRecordController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.FinancialRecord;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.PatientFormController;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
@Page(name="Tài chính", icon="images/finance.png", fxml="manage/finance.fxml")
public class FinancePage extends AbstractPage {

    @FXML private TableView<FinancialRecord> tableViewRecord;
    @FXML private TableColumn<FinancialRecord, String> dateColumn;
    @FXML private TableColumn<FinancialRecord, String> amountColumn;
    @FXML private TableColumn<FinancialRecord, String> patientColumn;
    @FXML private TableColumn<FinancialRecord, String> descriptionColumn;
    @FXML private TableColumn<FinancialRecord, Void> actionColumn;
    @FXML private TextField searchField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    private List<FinancialRecord> recordList;
    private FinancialRecordController recordController;

    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        recordController = new FinancialRecordController(em);
        setupTableColumns();
        loadRecords();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    private void setupTableColumns() {
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        patientColumn.setCellValueFactory((cell) -> new SimpleStringProperty(cell.getValue().getPatient().getName()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        loadActionColumn();
    }
    private void loadActionColumn(){
    actionColumn.setCellFactory(param -> new TableCell<>() {
        private final Button editButton;
        private final Button deleteButton;

        {
            ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/edit.png")));
            editIcon.setFitHeight(22);
            editIcon.setFitWidth(22);

            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete_1.png")));
            deleteIcon.setFitHeight(22);
            deleteIcon.setFitWidth(22);


            editButton = new Button("", editIcon);
            deleteButton = new Button("", deleteIcon);
            editButton.getStyleClass().add("button-icon");
            deleteButton.getStyleClass().add("button-icon");

            editButton.setOnAction(event -> {
                FinancialRecord record = getTableView().getItems().get(getIndex());
                handleEditRecord(record);
            });

            deleteButton.setOnAction(event -> {
                FinancialRecord record = getTableView().getItems().get(getIndex());
                handleDeleteRecord(record);
            });
        }


        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(new HBox(5, editButton, deleteButton));
            }
        }

    });
}
    private void loadRecords() {
        recordList = recordController.getAllFinancialRecords();
        tableViewRecord.getItems().setAll(recordList);
    }
    @FXML
    private void handleFilter() {
        System.out.println("Đang lọc...");

        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            tableViewRecord.getItems().setAll(recordList);
            loadActionColumn();
            return;
        }

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        if (toDate.isBefore(fromDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        List<FinancialRecord> filteredList = recordList.stream()
                .filter(patient -> {
                    LocalDateTime createdAt = patient.getCreatedAt();
                    return !createdAt.toLocalDate().isBefore(fromDate) && !createdAt.toLocalDate().isAfter(toDate);
                })
                .collect(Collectors.toList());

        tableViewRecord.getItems().setAll(filteredList);
        loadActionColumn();
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
            tableViewRecord.getItems().setAll(recordList);
        } else {
            List<FinancialRecord> filteredList = recordList.stream()
                    .filter(patient -> matchesSearch(patient, trimmedKeyword))
                    .toList();

            tableViewRecord.getItems().setAll(filteredList);
        }

        loadActionColumn();
    }

    private boolean matchesSearch(FinancialRecord record, String keyword) {
        String[] keywords = keyword.split("\\s+");

        String combinedData = (record.getDescription());

        return Arrays.stream(keywords).allMatch(combinedData::contains);
    }


    private void handleDeleteRecord(FinancialRecord record) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa record này?");

        ButtonType buttonYes = new ButtonType("Có", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("Không", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                recordController.deleteFinancialRecord(record.getFinanceId());
                loadRecords();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Xóa bệnh nhân thành công!", ButtonType.OK);
                alert1.showAndWait();
            }
        });
    }

    @FXML
    private void handleAddPatient() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/financeform.fxml"));

            AnchorPane root = loader.load();
            PatientFormController patientFormController = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Add New Patient");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadRecords();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Không thể load file FXML: " + ex.getMessage());
        }
    }
    @FXML
    private void handleEditRecord(FinancialRecord record) {
        // try {

        //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/financeform.fxml"));

        //     AnchorPane root = loader.load();
        //     PatientFormController patientFormController = loader.getController();
        //     patientFormController.setPatient(record);
        //     Stage stage = new Stage();
        //     stage.setTitle("Edit New Patient");
        //     stage.setScene(new Scene(root));
        //     stage.initModality(Modality.APPLICATION_MODAL);
        //     stage.showAndWait();
        //     loadRecords();
        // } catch (IOException ex) {
        //     ex.printStackTrace();
        //     System.out.println("Không thể load file FXML: " + ex.getMessage());
        // }
    }
}
