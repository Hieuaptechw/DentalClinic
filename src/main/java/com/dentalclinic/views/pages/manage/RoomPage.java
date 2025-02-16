package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.BranchController;
import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.controllers.RoomController;
import com.dentalclinic.entities.Branch;
import com.dentalclinic.entities.Patient;
import com.dentalclinic.entities.Room;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.BranchFormController;
import com.dentalclinic.views.pages.form.FinancialFormController;
import com.dentalclinic.views.pages.form.RoomFormController;
import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Page(name = "Phòng Khám", icon = "images/roomclinic.png", fxml = "manage/roomclinic.fxml")
public class RoomPage extends AbstractPage {
    @FXML private TableView<Room> tableViewRooms;
    @FXML private TableView<Branch> tableViewBranches;
    @FXML private TableColumn<Branch, String> nameColumnBranch;
    @FXML private TableColumn<Branch, String>  phoneColumnBranch;
    @FXML private TableColumn<Branch, String> addressColumnBranch;
    @FXML private TableColumn<Branch, Void> actionColumnBranch;
    @FXML private TableColumn<Room, String> nameBranchColumnRoom;
    @FXML private TableColumn<Room, String>  roomNumberColumnRoom;
    @FXML private TableColumn<Room, String> roomTypeColumnRoom;
    @FXML private TableColumn<Room, Void> actionColumnRoom;
    @FXML private TextField branchNameField;
    @FXML private TextField branchPhoneField;
    @FXML private TextField roomNumber;
    @FXML private TextArea addressField;
    @FXML private ComboBox<String> roomType;
    @FXML private ComboBox<Branch> branchName;
    @FXML private TextField searchBranchField;
    @FXML private TextField searchRoomField;
    private ObservableList<Branch> branchesList = FXCollections.observableArrayList();
    private ObservableList<Room> roomsList = FXCollections.observableArrayList();
    private BranchController branchController;
    private RoomController roomController;
    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        branchController = new BranchController(em);
        roomController = new RoomController(em);
        setupTableColumns();
        loadData();
        setupComboBoxListeners();
        searchRoomField.textProperty().addListener((observable, oldValue, newValue) -> handleSearchRoom(newValue));
        searchBranchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearchBranch(newValue));
    }
    private void loadData(){
        List<Branch> branches = branchController.getAllBranches().stream().sorted(Comparator.comparing(Branch::getUpdatedAt).reversed()).toList();
        List<Room> rooms = roomController.getAllRooms().stream().sorted(Comparator.comparing(Room::getUpdatedAt).reversed()).toList();;
        branchesList.setAll(branches);
        roomsList.setAll(rooms);
        tableViewRooms.setItems(roomsList);
        tableViewBranches.setItems(branchesList);
        setupComboBox();

    }
    private void setupTableColumns() {
         nameColumnBranch.setCellValueFactory(new PropertyValueFactory<>("branchName"));
         phoneColumnBranch.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumnBranch.setCellValueFactory(new PropertyValueFactory<>("address"));
        loadActionColumnBranch();
        nameBranchColumnRoom.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getBranch().getBranchName()));
        roomNumberColumnRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumnRoom.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        loadActionColumnRoom();

    }
    private void loadActionColumnBranch(){
        actionColumnBranch.setCellFactory(param -> new TableCell<>() {
            private final Button editButton;
            private final Button deleteButton;
            private final Button viewButton;

            {
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/edit.png")));
                editIcon.setFitHeight(22);
                editIcon.setFitWidth(22);

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete_1.png")));
                deleteIcon.setFitHeight(22);
                deleteIcon.setFitWidth(22);

                ImageView viewIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/eye.png")));
                viewIcon.setFitHeight(22);
                viewIcon.setFitWidth(22);


                editButton = new Button("", editIcon);
                deleteButton = new Button("", deleteIcon);
                viewButton = new Button("", viewIcon);
                editButton.getStyleClass().add("button-icon");
                deleteButton.getStyleClass().add("button-icon");
                viewButton.getStyleClass().add("button-icon");

                editButton.setOnAction(event -> {
                    Branch branch = getTableView().getItems().get(getIndex());
                    handleEditBranch(branch);
                });

                deleteButton.setOnAction(event -> {
                       Branch branch = getTableView().getItems().get(getIndex());
                    handleDeleteBranch(branch);
                });

                viewButton.setOnAction(event -> {
//                    Patient patient = getTableView().getItems().get(getIndex());
//                    // handleViewPatient(patient);
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editButton , viewButton,deleteButton));
                }
            }

        });
    }
    private void handleDeleteRoom(Room room) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this room?");
        alert.setContentText("Name: " + room.getRoomNumber());

        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                roomController.deleteRoom(room.getRoomId());
                loadData();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Room deleted successfully!", ButtonType.OK);
                alert1.showAndWait();
            }
        });
    }
    private void handleDeleteBranch(Branch branch) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this branch?");
        alert.setContentText("Name: " + branch.getBranchName());

        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                branchController.deleteBranch(branch.getBranchId());
                loadData();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Branch deleted successfully!", ButtonType.OK);
                alert1.showAndWait();
            }
        });
    }
    private void loadActionColumnRoom(){
        actionColumnRoom.setCellFactory(param -> new TableCell<>() {
            private final Button editButton;
            private final Button deleteButton;
            private final Button viewButton;

            {
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/edit.png")));
                editIcon.setFitHeight(22);
                editIcon.setFitWidth(22);

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete_1.png")));
                deleteIcon.setFitHeight(22);
                deleteIcon.setFitWidth(22);

                ImageView viewIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/eye.png")));
                viewIcon.setFitHeight(22);
                viewIcon.setFitWidth(22);


                editButton = new Button("", editIcon);
                deleteButton = new Button("", deleteIcon);
                viewButton = new Button("", viewIcon);
                editButton.getStyleClass().add("button-icon");
                deleteButton.getStyleClass().add("button-icon");
                viewButton.getStyleClass().add("button-icon");

                editButton.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    handleEditRoom(room);
                });

                deleteButton.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    handleDeleteRoom(room);

                });

                viewButton.setOnAction(event -> {

                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editButton , viewButton,deleteButton));
                }
            }

        });
    }
    private void handleEditRoom(Room room) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/roomform.fxml"));
            VBox root = (VBox) loader.load();
            RoomFormController roomFormController = loader.getController();
            roomFormController.setRoom(room,branchesList);
            Stage stage = new Stage();
            stage.setTitle("Edit Branch");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadData();

        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void handleEditBranch(Branch branch){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/branchform.fxml"));
            VBox root = (VBox) loader.load();
            BranchFormController branchFormController = loader.getController();
            branchFormController.setBranch(branch);
            Stage stage = new Stage();
            stage.setTitle("Edit Branch");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadData();

        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleAddBranch() {
        String branchName = branchNameField.getText().trim();
        String branchPhone = branchPhoneField.getText().trim();
        String address = addressField.getText().trim();
        if (branchName.isEmpty() || branchPhone.isEmpty() || address.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill out all fields");
            alert.showAndWait();
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        Branch branch = new Branch(branchName, address, branchPhone, now, now);
        branchController.addBranch(branch);
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Branch added successfully!");
        successAlert.showAndWait();
        handleClearBranch();
        loadData();

    }
    @FXML
    public void handleAddRoom() {
        String selectedRoomType = (String) roomType.getSelectionModel().getSelectedItem();
        String roomNb = roomNumber.getText().trim();
        Branch selectedBranch = (Branch) branchName.getSelectionModel().getSelectedItem();
        if (selectedRoomType == null || roomNb.isEmpty() || selectedBranch == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ thông tin!");
            alert.setTitle("Lỗi");
            alert.show();
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        Room room = new Room(roomNb, selectedBranch, selectedRoomType, now, now);
        roomController.addRoom(room);
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Thêm phòng thành công!");
        successAlert.setTitle("Thành công");
        successAlert.show();
        handleClearRoom();
        loadData();
    }
    @FXML
    public void handleClearRoom() {
        if (!roomType.getItems().isEmpty()) {
            roomType.setValue(roomType.getItems().get(roomType.getItems().size() - 1));
        } else {
            roomType.setValue(null);
        }

        if (!branchName.getItems().isEmpty()) {
            branchName.setValue(branchName.getItems().get(branchName.getItems().size() - 1));
        } else {
            branchName.setValue(null);
        }
        roomNumber.clear();
    }
    @FXML
    public void handleClearBranch() {
       branchNameField.clear();
       branchPhoneField.clear();
       addressField.clear();
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
    private void setupComboBox(){
       ObservableList<String> roomTypes = FXCollections.observableArrayList("VIP", "Standard", "Deluxe","RoomType");
       ObservableList<Branch> branchObservableList = FXCollections.observableArrayList(branchesList);

       branchName.setItems(branchObservableList);
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
    @FXML
    private void handleSearchBranch(String keyword) {
        String trimmedKeyword = keyword.toLowerCase().trim();

        if (trimmedKeyword.isEmpty()) {
            tableViewBranches.setItems(branchesList);
        } else {
            List<Branch> filteredList = branchesList.stream()
                    .filter(branch -> matchesSearchBranch(branch, trimmedKeyword))
                    .toList();

            tableViewBranches.setItems(FXCollections.observableArrayList(filteredList));
        }

        loadActionColumnBranch();
    }

    private boolean matchesSearchBranch(Branch branch, String keyword) {
        String[] keywords = keyword.split("\\s+");
        String combinedData = (branch.getBranchName() + " " +
                branch.getAddress() + " " +
                branch.getAddress()).toLowerCase();

        return Arrays.stream(keywords).allMatch(combinedData::contains);
    }
    private void handleSearchRoom(String keyword) {
        String trimmedKeyword = keyword.toLowerCase().trim();

        if (trimmedKeyword.isEmpty()) {
            tableViewRooms.setItems(roomsList);
        } else {
            List<Room> filteredList = roomsList.stream()
                    .filter(r -> matchesSearchRoom(r, trimmedKeyword))
                    .toList();

            tableViewRooms.setItems(FXCollections.observableArrayList(filteredList));
        }

        loadActionColumnBranch();
    }

    private boolean matchesSearchRoom(Room room, String keyword) {
        String[] keywords = keyword.split("\\s+");
        String combinedData = (room.getRoomNumber() + " " +
                room.getRoomType() + " " +
                room.getBranch().getBranchName()).toLowerCase();

        return Arrays.stream(keywords).allMatch(combinedData::contains);
    }

}
