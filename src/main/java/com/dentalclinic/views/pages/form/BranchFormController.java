package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.BranchController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.entities.Branch;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BranchFormController {
    @FXML private TextField branchName;
    @FXML private TextField branchPhone;
    @FXML private TextArea branchAddress;
    private BranchController branchController;
private Branch branchSelected;
    @FXML
    private void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        branchController = new BranchController(em);
   }
   public void setBranch(Branch branch) {
       branchSelected = branch;
       branchName.setText(branch.getBranchName());
       branchPhone.setText(branch.getPhone());
       branchAddress.setText(branch.getAddress());
   }
    @FXML
    public void handleUpdate() {
        String name = branchName.getText().trim();
        String phone = branchPhone.getText().trim();
        String address = branchAddress.getText().trim();
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng nhập đầy đủ thông tin!");
            alert.showAndWait();
            return;
        }
        LocalDateTime createdAt = (branchSelected != null) ? branchSelected.getCreatedAt() : LocalDateTime.now();
        Branch branch = new Branch(name, address, phone, createdAt, LocalDateTime.now());
        if (branchSelected != null) {
            branch.setBranchId(branchSelected.getBranchId());
        }
        branchController.updateBranch(branch);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công!");
        alert.showAndWait();

        Stage stage = (Stage) branchName.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleClear(){

    }
}
