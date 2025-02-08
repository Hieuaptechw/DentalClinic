package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.RoomController;
import com.dentalclinic.entities.Branch;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.entities.Room;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.util.List;

@Page(name="Phòng Khám", icon="images/roomclinic.png", fxml="manage/roomclinic.fxml")
public class RoomPage extends AbstractPage {
    @FXML
    public Button addButton;

    @FXML
    public TextField searchField;

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
    public TableColumn<Room, LocalDateTime> createColumn;

    @FXML
    public TableColumn<Room, LocalDateTime> updateColumn;

    @FXML
    public TableColumn<Room, Void> actionColumn;
    
    @FXML
    public TableView<Branch> branchTable;

    @FXML
    public TableColumn<Branch, Long> branchIdColumn;

    @FXML
    public TableColumn<Branch, String> branchNameColumn;

    @FXML
    public TableColumn<Branch, String> addressColumn;

    @FXML
    public TableColumn<Branch, String> phoneColumn;

    @FXML
    public TableColumn<Branch, LocalDateTime> branchCreateAtColumn;

    @FXML
    public TableColumn<Branch, LocalDateTime> branchUpdateAtColumn;

    @FXML
    public TableColumn<Branch, Void> branchActionColumn;

    private RoomController roomController;

    public RoomPage() {
        EntityManager em = DatabaseController.getEntityManager();
        this.roomController = new RoomController(em);
    }

    ObservableList<Room> roomsObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadRoom();
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        roomBranchIdColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        createColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updateColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        actionColumn.setCellFactory(KParameter -> new TableCell<Room, Void>() {
            private final Button editButton;
            private final Button deleteButton;

            {
                // Tạo icon sửa
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/pen.png")));
                editIcon.setFitWidth(20);
                editIcon.setFitHeight(20);
                editButton = new Button("", editIcon); // Nút không có text, chỉ có icon
                editButton.setStyle("-fx-cursor: hand;");

                editButton.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    if (room != null) {
                        // loadViewEditProduct(inventory);
                    }
                });

                // Tạo icon xóa
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete.png")));
                deleteIcon.setFitWidth(20);
                deleteIcon.setFitHeight(20);
                deleteButton = new Button("", deleteIcon);
                deleteButton.setStyle("-fx-cursor: hand;");

                deleteButton.setOnAction(event -> {
                    Room room = getTableRow().getItem();
                    if (room != null) {
                        // handleDelete(inventory);
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

        roomTable.setItems(roomsObservableList);
    }

    public void loadRoom() {
        List<Room> rooms = roomController.getAllRooms();
        roomsObservableList.addAll(rooms);
    }

}
