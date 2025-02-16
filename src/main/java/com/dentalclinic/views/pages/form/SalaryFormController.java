package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.MedicalRecordController;
import com.dentalclinic.controllers.SalaryController;
import com.dentalclinic.entities.MedicalRecord;
import com.dentalclinic.entities.Salary;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;


public class SalaryFormController {
    @FXML
    private TextField editName, editAmount;

    @FXML
    private DatePicker editDate;

    private SalaryController salaryController;
    private Salary salary;

    public SalaryFormController(){
        EntityManager em = DatabaseController.getEntityManager();
        this.salaryController = new SalaryController(em);
    }

    public void setSalaryDate(Salary salary){
        this.salary = salary;
        editName.setText(salary.getStaff().getName());
        editAmount.setText(String.valueOf(salary.getAmount()));
        editDate.setValue(editDate.getValue());
    }

    @FXML
    private void handleSave(){
        if(salary != null){
            String amount = editAmount.getText().trim();
            String date = editDate.getValue().toString();

            salary.setAmount(Double.parseDouble(amount));
            salary.setPaymentDate(LocalDate.parse(date).atStartOfDay());

            salaryController.updateSalary(salary);
            alertMessage(Alert.AlertType.INFORMATION, "Cập nhật lương", "Lương đã được cập nhật");
        }
    }

    public void alertMessage(Alert.AlertType type, String title, String headerText){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
}
