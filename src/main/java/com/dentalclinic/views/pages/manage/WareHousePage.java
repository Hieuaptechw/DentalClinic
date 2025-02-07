package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.InventoryController;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.EditInventoryPage;
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
                editIcon.setFitWidth(30);
                editIcon.setFitHeight(30);
                editButton = new Button("", editIcon);
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                editButton.setOnAction(event -> {
                    Inventory inventory = getTableView().getItems().get(getIndex());
                    if (inventory != null) {
                        handleEditInventory(inventory);
                    }
                });

                // Tạo icon xóa
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete.png")));
                deleteIcon.setFitWidth(30);
                deleteIcon.setFitHeight(30);
                deleteButton = new Button("", deleteIcon);
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                deleteButton.setOnAction(event -> {
                    Inventory inventory = getTableRow().getItem();
                    if (inventory != null) {
                        handleDelete(inventory);
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

    public void handleDelete(Inventory inventory){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Xóa sản phẩm");
        alert.setContentText("Bạn có chắc chắn muốn xóa sản phẩm: " + inventory.getItemName() + "?");
        alert.showAndWait().ifPresent(response -> {
            if(response == ButtonType.YES){
                inventoryController.handleDeleteInventory(inventory.getInventoryId());
                productsObservableList.remove(inventory);
            }
        });

    }

    public void handleEditInventory(Inventory inventory){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/EditInventory.fxml"));
            Parent root = loader.load();
            EditInventoryPage editInventoryPage = loader.getController();
            editInventoryPage.setInventoryData(inventory, productsObservableList);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sửa Sản Phẩm");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInventory(){
        List<Inventory> inventories = inventoryController.getAllInventory();
        productsObservableList.addAll(inventories);
    }




}
