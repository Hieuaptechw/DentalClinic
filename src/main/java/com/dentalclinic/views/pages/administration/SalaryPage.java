package com.dentalclinic.views.pages.administration;

import com.dentalclinic.entities.Salary;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@Page(name="Lương", icon="images/revenue.png", fxml="administration/salary.fxml")
public class SalaryPage extends AbstractPage {
    @FXML
    private TableView<Salary> salaryTable;

    @FXML
    private TableColumn<Salary, String> nameColumn;




}
