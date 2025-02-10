package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.BranchController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.entities.Branch;
import com.dentalclinic.entities.Room;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;

@Page(name = "Add New Branch", fxml = "manage/addNewBranch.fxml")
public class AddNewBranch extends AbstractPage {
    @FXML private Label titleLabel;
    @FXML private TextField branchNameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private Button confirmButton;

    @FXML private Label branchNameError;
    @FXML private Label addressError;
    @FXML private Label phoneError;

    private BranchController branchController;
    private Runnable onAddBranchSuccess;
    private Runnable onEditBranchSuccess;
    private Branch currentBranch;
    private boolean isEditMode = false;

    public AddNewBranch() {
        EntityManager em = DatabaseController.getEntityManager();
        this.branchController = new BranchController(em);
    }

    public void setOnAddBranchSuccess(Runnable onAddBranchSuccess) {
        this.onAddBranchSuccess = onAddBranchSuccess;
    }

    public void setOnEditBranchSuccess(Runnable onEditBranchSuccess) {
        this.onEditBranchSuccess = onEditBranchSuccess;
    }

    @FXML
    public void initialize() { confirmButton.setOnAction(event -> handleSubmit());}

    private void handleSubmit() {
        boolean valid = validateInputs();

        if (valid) {
            if (isEditMode) {
                currentBranch.setBranchName(branchNameField.getText());
                currentBranch.setAddress(addressField.getText());
                currentBranch.setPhone(phoneField.getText());
                currentBranch.setUpdatedAt(LocalDateTime.now());

                branchController.updateBranch(currentBranch);

                if (onEditBranchSuccess != null) {
                    onEditBranchSuccess.run();
                }

                showAlert("Success", "Branch updated successfully.", Alert.AlertType.INFORMATION);
            } else {
                Branch newBranch = new Branch();
                newBranch.setBranchName(branchNameField.getText());
                newBranch.setAddress(addressField.getText());
                newBranch.setPhone(phoneField.getText());
                newBranch.setCreatedAt(LocalDateTime.now());
                newBranch.setUpdatedAt(LocalDateTime.now());

                branchController.addBranch(newBranch);

                if (onAddBranchSuccess != null) {
                    onAddBranchSuccess.run();
                }

                showAlert("Success", "Branch added successfully.", Alert.AlertType.INFORMATION);
            }

            clearFields();
            closeWindow();
        }
    }

    public void loadBranchForEditing(Branch branch) {
        this.currentBranch = branch;
        this.isEditMode = true;
        titleLabel.setText("Edit Branch");

        branchNameField.setText(branch.getBranchName());
        addressField.setText(branch.getAddress());
        phoneField.setText(branch.getPhone());
    }

    private boolean validateInputs() {
        boolean valid = true;

        if (branchNameField.getText().isEmpty()) {
            branchNameError.setText("Branch name is required.");
            valid = false;
        } else {
            branchNameError.setText("");
        }

        if (addressField.getText().isEmpty()) {
            addressError.setText("Address is required.");
            valid = false;
        } else {
            addressError.setText("");
        }

        if (phoneField.getText().isEmpty()) {
            phoneError.setText("Phone is required.");
            valid = false;
        } else {
            phoneError.setText("");
        }

        return valid;
    }

    private void closeWindow() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        branchNameField.clear();
        addressField.clear();
        phoneField.clear();
    }
}
