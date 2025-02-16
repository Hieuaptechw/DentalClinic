package com.dentalclinic.views.pages.administration;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.SalaryController;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.entities.Salary;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.PatientRecordFormController;
import com.dentalclinic.views.pages.form.SalaryFormController;
import jakarta.persistence.EntityManager;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Page(name="Lương", icon="images/revenue.png", fxml="administration/salary.fxml")
public class SalaryPage extends AbstractPage {
    @FXML
    private TableView<Salary> salaryTable;

    @FXML
    private TextField emailField, nameField, salaryField ;

    @FXML
    private DatePicker dateField;

    @FXML
    private TableColumn<Salary, String> nameColumn, amountColumn,paymentColumn;

    @FXML
    private TableColumn<Salary, Void> actionColumn;

    private ObservableList<Salary> salaryObservableList = FXCollections.observableArrayList();

    private SalaryController salaryController;

    public void initialize(){
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        salaryController = new SalaryController(em);
        setupTableColumn();
        loadSalaryRecord();
    }

    public void setupTableColumn(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("staff"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        loadActionColumn(actionColumn);
    }

    public void loadSalaryRecord(){
        List<Salary> salaries = salaryController.getAllSalaryRecord();
        salaryObservableList.setAll(salaries);
        salaryTable.setItems(salaryObservableList);
    }


    @FXML
    public void handleAddSalary(){
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        double newSalary = Double.parseDouble(salaryField.getText().trim());
        LocalDate date = dateField.getValue();

        Staff staff = salaryController.findStaffByEmailAndName(email, name);

        if(staff != null){
            Salary existsSalary = salaryController.findSalaryByStaff(staff);
            if(existsSalary != null){
                existsSalary.setAmount(existsSalary.getAmount() + newSalary);
                existsSalary.setPaymentDate(date.atStartOfDay());
                salaryController.updateSalary(existsSalary);
                alertMessage(Alert.AlertType.INFORMATION, "Thêm lương", "Thêm lương thành công");
            }else {
                Salary salary = new Salary();
                salary.setStaff(staff);
                salary.setAmount(newSalary);
                salary.setPaymentDate(date.atStartOfDay());
                salaryController.handleAddSalary(salary);
                alertMessage(Alert.AlertType.INFORMATION, "Thêm lương", "Thêm thành công");
            }
            loadSalaryRecord();
        }else {
            alertMessage(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy nhân viên này");
        }
    }

    public void alertMessage(Alert.AlertType type, String title, String headerText){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }


    private void loadActionColumn(TableColumn<Salary, Void> colNameActio) {
        colNameActio.setCellFactory(param -> new TableCell<>() {
            private final Button editButton;
            private final Button deleteButton;
            private final Button printButton;
            {
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/edit.png")));
                editIcon.setFitHeight(22);
                editIcon.setFitWidth(22);

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete_1.png")));
                deleteIcon.setFitHeight(22);
                deleteIcon.setFitWidth(22);

                ImageView printIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/printer.png")));
                printIcon.setFitHeight(22);
                printIcon.setFitWidth(22);


                editButton = new Button("", editIcon);
                deleteButton = new Button("", deleteIcon);
                printButton = new Button("", printIcon);
                editButton.getStyleClass().add("button-icon");
                deleteButton.getStyleClass().add("button-icon");
                printButton.getStyleClass().add("button-icon");

                editButton.setOnAction(event -> {
                    Salary salary = getTableView().getItems().get(getIndex());
                    handleEditPatientRecord(salary);
                    System.out.println(salary);

                });

                deleteButton.setOnAction(event -> {
                    Salary salary = getTableView().getItems().get(getIndex());
                    handleDelete(salary);
                });

                printButton.setOnAction(event -> {
                    Salary salary = getTableView().getItems().get(getIndex());

                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, printButton,editButton,deleteButton));
                }
            }

        });
    }

    public void handleDelete(Salary salary){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Xóa lương");
        alert.setContentText("Bạn có chắc chắn muốn xóa : " + salary.getStaff().getName() + "?");
        alert.showAndWait().ifPresent(response -> {
            if(response == ButtonType.OK){
                salaryController.handleDeleteSalary(salary.getSalaryId());
                salaryObservableList.remove(salary);
            }
        });
    }



    public void handleEditPatientRecord(Salary salary){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/editSalaryForm.fxml"));
            Parent root = loader.load();
            SalaryFormController salaryFormController = loader.getController();
            salaryFormController.setSalaryDate(salary);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sửa bệnh án");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadSalaryRecord();
            System.out.println("Đã load");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

