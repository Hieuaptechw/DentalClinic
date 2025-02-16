package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.BranchController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.RoomController;
import com.dentalclinic.entities.Branch;
import com.dentalclinic.entities.Room;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.util.List;

public class RoomFormController {
    private ObservableList<Branch> branchList;
    private RoomController roomController;
    private BranchController branchController;
    private Room roomSelected;
    @FXML private ComboBox<String> roomType;
    @FXML private ComboBox<Branch> branchName;
    @FXML private TextField roomNumber;
    @FXML
    public void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        roomController = new RoomController(em);
    setupComboBoxListeners();
    }

    @FXML
    public void handleUpdate() {
        String selectedRoomType = (String) roomType.getSelectionModel().getSelectedItem();
        String roomNb = roomNumber.getText().trim();
        Branch selectedBranch = (Branch) branchName.getSelectionModel().getSelectedItem();

        if (selectedRoomType == null || roomNb.isEmpty() || selectedBranch == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ thông tin!");
            alert.setTitle("Lỗi");
            alert.show();
            return;
        }
        LocalDateTime createdAt = (roomSelected != null) ? roomSelected.getCreatedAt() : LocalDateTime.now();
        Room roomUpdate = new Room(roomNb, selectedBranch, selectedRoomType, createdAt, LocalDateTime.now());
        if (roomSelected != null) {
            roomUpdate.setRoomId(roomSelected.getRoomId());
        }
        roomController.updateRoom(roomUpdate);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công!");
        alert.showAndWait();
        Stage stage = (Stage) roomNumber.getScene().getWindow();
        stage.close();
    }


    @FXML
    public void handleClear() {
        roomSelected = null;
        System.out.println("Form cleared!");
    }

    public void setRoom(Room room, ObservableList<Branch> branchesList) {
        this.branchList = branchesList;
        this.roomSelected = room;
        roomNumber.setText(roomSelected.getRoomNumber());
        roomType.setValue(roomSelected.getRoomType());
        setupComboBox();

        for (Branch branch : branchList) {
            if (branch.getBranchId()==(roomSelected.getBranch().getBranchId())) {
                branchName.setValue(branch);
                break;
            }
        }
    }

    private void setupComboBoxListeners() {
        branchName.setOnAction(event -> {
            Branch selectedBranch = branchName.getSelectionModel().getSelectedItem();
            if (selectedBranch != null) {
                System.out.println("Branch được chọn: " + selectedBranch.getBranchName());
            }
        });

        roomType.setOnAction(event -> {
            String selectedRoomType = roomType.getSelectionModel().getSelectedItem();
            if (selectedRoomType != null) {
                System.out.println("Phòng được chọn: " + selectedRoomType);
            }
        });
    }
    private void setupComboBox() {
        ObservableList<String> roomTypes = FXCollections.observableArrayList("VIP", "Standard", "Deluxe", "RoomType");
        if (branchList == null) {
            branchList = FXCollections.observableArrayList();
        }

        branchName.setItems(branchList);
        branchName.setConverter(new StringConverter<Branch>() {
            @Override
            public String toString(Branch branch) {
                return branch != null ? branch.getBranchName() : "";
            }

            @Override
            public Branch fromString(String string) {
                return null;
            }
        });

        roomType.setItems(roomTypes);
    }



}
