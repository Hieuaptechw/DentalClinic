package com.dentalclinic.views.pages.administration;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.SalaryController;
import com.dentalclinic.entities.Salary;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

@Page(name="Lương", icon="images/revenue.png", fxml="administration/salary.fxml")
public class SalaryPage extends AbstractPage {
    @FXML
    private TableView<Salary> salaryTable;

    @FXML
    private TableColumn<Salary, String> nameColumn,idColumn, amountColumn,paymentColumn, roleColumn;

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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("salaryId"));
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaff().getName()));
        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaff().getRole().toString()));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
    }

    public void loadSalaryRecord(){
        List<Salary> salaries = salaryController.getAllSalaryRecord();
        salaryObservableList.setAll(salaries);
        salaryTable.setItems(salaryObservableList);
    }







}
