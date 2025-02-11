package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.BranchController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.RoomController;
import com.dentalclinic.entities.Branch;
import com.dentalclinic.entities.Room;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Page(name = "Phòng Khám", icon = "images/roomclinic.png", fxml = "manage/roomclinic.fxml")
public class RoomPage extends AbstractPage {
    @FXML
    public Button addRoomButton;
    @FXML
    public TextField roomSearchField;
    @FXML
    public TableView<Room> roomTable;
    @FXML
    public TableColumn<Room, Long> roomIdColumn;
    @FXML
    public TableColumn<Room, String> roomNumberColumn;
    @FXML
    public TableColumn<Room, Long> roomBranchIdColumn;
    @FXML
    public TableColumn<Room, String> roomTypeColumn;
    @FXML
    public TableColumn<Room, LocalDateTime> roomCreateColumn;
    @FXML
    public TableColumn<Room, LocalDateTime> roomUpdateColumn;
    @FXML
    public TableColumn<Room, Void> roomActionColumn;

    @FXML
    public TableView<Branch> branchTable;
    @FXML
    public TableColumn<Branch, Long> branchIdColumn;
    @FXML
    public TableColumn<Branch, String> branchNameColumn;
    @FXML
    public TableColumn<Branch, String> branchAddressColumn;
    @FXML
    public TableColumn<Branch, String> branchPhoneColumn;
    @FXML
    public TableColumn<Branch, LocalDateTime> branchCreateColumn;
    @FXML
    public TableColumn<Branch, LocalDateTime> branchUpdateColumn;
    @FXML
    public TableColumn<Branch, Void> branchActionColumn;
    @FXML
    public Button addBranchButton;
    @FXML
    public TextField branchSearchField;

    private RoomController roomController;
    private BranchController branchController;

    public RoomPage() {
        EntityManager em = DatabaseController.getEntityManager();
        this.roomController = new RoomController(em);
        this.branchController = new BranchController(em);
    }

    ObservableList<Room> roomsObservableList = FXCollections.observableArrayList();
    ObservableList<Branch> branchesObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadRoom();
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        roomBranchIdColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomCreateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        roomUpdateColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        createActionColumn(roomActionColumn);

        loadBranch();
        branchIdColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        branchNameColumn.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        branchAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        branchPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        branchCreateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        branchUpdateColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        createActionColumn(branchActionColumn);

        roomTable.setItems(roomsObservableList);
        branchTable.setItems(branchesObservableList);

        roomSearchField.textProperty().addListener((observable, oldValue, newValue) -> handleRoomSearch(newValue));
        branchSearchField.textProperty().addListener((observable, oldValue, newValue) -> handleBranchSearch(newValue));

        addRoomButton.setOnAction(e -> showAddRoomForm());
        addBranchButton.setOnAction(e -> showAddBranchForm());
    }

    private <T> void createActionColumn(TableColumn<T, Void> actionColumn) {
        actionColumn.setCellFactory(param -> new TableCell<T, Void>() {
            private final Button editButton;
            private final Button deleteButton;

            {
                ImageView editIcon = new ImageView((new Image(getClass().getResourceAsStream("/com/dentalclinic/images/pen.png"))));
                editIcon.setFitWidth(20);
                editIcon.setFitHeight(20);
                editButton = new Button("", editIcon);
                editButton.setStyle("-fx-cursor: hand;");

                editButton.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        if (item instanceof Room) {
                            showEditRoomForm((Room) item);
                        } else if (item instanceof Branch) {
                            showEditBranchForm((Branch) item);
                        }
                    }
                });

                ImageView deleteIcon = new ImageView((new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete.png"))));
                deleteIcon.setFitWidth(20);
                deleteIcon.setFitHeight(20);
                deleteButton = new Button("", deleteIcon);
                deleteButton.setStyle("-fx-cursor: hand;");

                deleteButton.setOnAction(event -> {
                    T item = getTableRow().getItem();
                    if (item != null) {
                        if (item instanceof Room) {
                            handleDeleteRoom((Room) item);
                        } else if (item instanceof Branch) {
                            handleDeleteBranch((Branch) item);
                        }
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

    // Phương thức sửa
    private void showEditRoomForm(Room room) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/manage/addNewRoom.fxml"));
            Parent root = loader.load();

            AddNewRoom controller = loader.getController();
            controller.loadRoomForEditing(room);

            controller.setOnEditRoomSuccess(() -> refreshRoomTable());

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 556, 482));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEditBranchForm(Branch branch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/manage/addNewBranch.fxml"));
            Parent root = loader.load();

            AddNewBranch controller = loader.getController();

            controller.setOnEditBranchSuccess(() -> refreshBranchTable());

            controller.loadBranchForEditing(branch);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 556, 482));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức xóa
    private void handleDeleteRoom(Room room) {
        String roomNumber = room.getRoomNumber();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Room");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa phòng " + roomNumber + " không?");
        alert.setContentText("Hành động này không thể hoàn tác!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            roomController.deleteRoom(room);
            roomsObservableList.remove(room);
            showAlert("Success", "Đã xóa phòng " + roomNumber + " thành công.", Alert.AlertType.INFORMATION);

            refreshRoomTable();
        }
    }

    private void handleDeleteBranch(Branch branch) {
        String branchName = branch.getBranchName();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Branch");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa chi nhánh " + branchName + " này?");
        alert.setContentText("Hành động này không thể hoàn tác!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            branchController.deleteBranch(branch);
            branchesObservableList.remove(branch);
            showAlert("Success", "Đã xóa " + branchName + " thành công.", Alert.AlertType.INFORMATION);

            refreshBranchTable();
        }
    }

    // Phương thức tìm kiếm
    private void handleRoomSearch(String searchText) {
        ObservableList<Room> filteredRooms = FXCollections.observableArrayList();
        if (searchText == null || searchText.trim().isEmpty()) {
            roomTable.setItems(roomsObservableList);
            return;
        }

        filteredRooms.addAll(roomsObservableList.stream()
                .filter(room -> String.valueOf(room.getBranchId()).contains(searchText) ||
                        room.getRoomNumber().contains(searchText) ||
                        room.getRoomType().contains(searchText))
                .toList());

        roomTable.setItems(filteredRooms);
        createActionColumn(roomActionColumn);
    }

    private void handleBranchSearch(String searchText) {
        ObservableList<Branch> filteredBranches = FXCollections.observableArrayList();
        if (searchText == null || searchText.trim().isEmpty()) {
            branchTable.setItems(branchesObservableList);
            return;
        }

        filteredBranches.addAll(branchesObservableList.stream()
                .filter(branch -> String.valueOf(branch.getBranchId()).contains(searchText) ||
                        branch.getBranchName().contains(searchText) ||
                        branch.getAddress().contains(searchText) ||
                        branch.getPhone().contains(searchText))
                .toList());

        branchTable.setItems(filteredBranches);
        createActionColumn(branchActionColumn);
    }

    private void showAddRoomForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/manage/addNewRoom.fxml"));
            Parent root = loader.load();

            AddNewRoom controller = loader.getController();

            controller.setOnAddRoomSuccess(() -> refreshRoomTable());

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 556, 333));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAddBranchForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/manage/addNewBranch.fxml"));
            Parent root = loader.load();

            AddNewBranch controller = loader.getController();

            controller.setOnAddBranchSuccess(() -> refreshBranchTable());

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 556, 301));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshRoomTable() {
        roomsObservableList.clear();
        roomsObservableList.addAll(roomController.getAllRooms());
        roomTable.setItems(roomsObservableList);
    }

    private void refreshBranchTable() {
        branchesObservableList.clear();
        branchesObservableList.addAll(branchController.getAllBranches());
        branchTable.setItems(branchesObservableList);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Phương thức để lấy dữ liệu từ danh sách trong db
    public void loadRoom() {
        List<Room> rooms = roomController.getAllRooms();
        roomsObservableList.addAll(rooms);
    }

    public void loadBranch() {
        List<Branch> branches = branchController.getAllBranches();
        branchesObservableList.addAll(branches);
    }
}
