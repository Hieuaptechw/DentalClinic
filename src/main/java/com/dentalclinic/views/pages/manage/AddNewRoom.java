package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.RoomController;
import com.dentalclinic.entities.Room;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;

@Page(name = "Add New Room", fxml = "manage/addNewRoom.fxml")
public class AddNewRoom extends AbstractPage {
    @FXML private TextField roomIdField;
    @FXML private TextField branchIdField;
    @FXML private TextField roomNumberField;
    @FXML private TextField roomTypeField;
    @FXML private Button confirmButton;

    @FXML private Label roomIdError;
    @FXML private Label branchIdError;
    @FXML private Label roomNumberError;
    @FXML private Label roomTypeError;
    @FXML private Label titleLabel;

    private RoomController roomController;
    private Room currentRoom;
    private boolean isEditMode = false; // Biến để phân biệt giữa thêm mới và chỉnh sửa
    private Runnable onAddRoomSuccess;
    private Runnable onEditRoomSuccess;

    public AddNewRoom() {
        EntityManager em = DatabaseController.getEntityManager();
        this.roomController = new RoomController(em);
    }

    public void setOnAddRoomSuccess(Runnable onAddRoomSuccess) {
        this.onAddRoomSuccess = onAddRoomSuccess;
    }

    public void setOnEditRoomSuccess(Runnable onEditRoomSuccess) {
        this.onEditRoomSuccess = onEditRoomSuccess;
    }

    @FXML
    public void initialize() {
        confirmButton.setOnAction(event -> handleSubmit());
    }

    public void loadRoomForEditing(Room room) {
        this.currentRoom = room;
        this.isEditMode = true;
        titleLabel.setText("Edit Room");
        roomIdField.setText(String.valueOf(room.getRoomId()));
        branchIdField.setText(String.valueOf(room.getBranchId()));
        roomNumberField.setText(room.getRoomNumber());
        roomTypeField.setText(room.getRoomType());
    }

    private void handleSubmit() {
        boolean valid = validateInputs();

        if (valid) {
            if (isEditMode) {
                currentRoom.setRoomNumber(roomNumberField.getText());
                currentRoom.setRoomType(roomTypeField.getText());
                currentRoom.setUpdatedAt(LocalDateTime.now());

                roomController.updateRoom(currentRoom);

                if (onEditRoomSuccess != null) {
                    onEditRoomSuccess.run();
                }

                showAlert("Success", "Room updated successfully.", Alert.AlertType.INFORMATION);
            } else {
                Room newRoom = new Room();
                newRoom.setBranchId(Long.parseLong(branchIdField.getText()));
                newRoom.setRoomNumber(roomNumberField.getText());
                newRoom.setRoomType(roomTypeField.getText());
                newRoom.setCreatedAt(LocalDateTime.now());
                newRoom.setUpdatedAt(LocalDateTime.now());

                roomController.addRoom(newRoom);

                if (onAddRoomSuccess != null) {
                    onAddRoomSuccess.run();
                }

                showAlert("Success", "Room added successfully.", Alert.AlertType.INFORMATION);
            }
            clearFields();
            closeWindow();
        }
    }

    private boolean validateInputs() {
        boolean valid = true;

        if (branchIdField.getText().isEmpty()) {
            branchIdError.setText("Branch ID is required");
            valid = false;
        } else {
            branchIdError.setText("");
        }

        if (roomNumberField.getText().isEmpty()) {
            roomNumberError.setText("Room Number is required");
            valid = false;
        } else {
            roomNumberError.setText("");
        }

        if (roomTypeField.getText().isEmpty()) {
            roomTypeError.setText("Room Type is required");
            valid = false;
        } else {
            roomTypeError.setText("");
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
        roomIdField.clear();
        branchIdField.clear();
        roomNumberField.clear();
        roomTypeField.clear();
    }
}
