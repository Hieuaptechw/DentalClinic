package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.InventoryController;
import com.dentalclinic.controllers.CalendarController;
import com.dentalclinic.entities.Inventory;
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
import javafx.util.Callback;

import java.util.List;

@Page(name="Kho", icon="images/warehouse.png", fxml="manage/warehouse.fxml")
public class WareHousePage extends AbstractPage {
    @FXML
    private TextField searchField;

    @FXML
    private TableView<Inventory> inventoryTable;

    @FXML
    private TableColumn<Inventory, Long> idColumn;

    @FXML
    private TableColumn<Inventory, String> nameColumn;

    @FXML
    private TableColumn<Inventory, Double> priceColumn;

    @FXML
    private TableColumn<Inventory, Integer> quantityColumn;

    @FXML
    private TableColumn<Inventory, String> supplierColumn;

    @FXML
    private TableColumn<Inventory, Void> actionColumn;

    private InventoryController inventoryController;

    public WareHousePage(){
        EntityManager em = DatabaseController.getEntityManager();
        this.inventoryController = new InventoryController(em);
    }

    ObservableList<Inventory> productsObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        loadInventory();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("inventoryId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        actionColumn.setCellFactory(KParameter -> new TableCell<Inventory, Void>() {
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
                    Inventory inventory = getTableView().getItems().get(getIndex());
                    if (inventory != null) {
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
                    Inventory inventory = getTableRow().getItem();
                    if (inventory != null) {
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


        inventoryTable.setItems(productsObservableList);
    }

    public void loadInventory(){
        List<Inventory> inventories = inventoryController.getAllInventory();
        productsObservableList.addAll(inventories);
    }



}
