package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.InventoryController;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.views.pages.manage.WareHousePage;
import jakarta.persistence.EntityManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


public class AddInventoryPage {
    @FXML
    private TextField addName;

    @FXML
    private TextField addPrice;

    @FXML
    private TextField addQuantity;

    @FXML
    private TextField addSupplier;

    private WareHousePage wareHousePage;

    private InventoryController inventoryController;

    public AddInventoryPage() {
        EntityManager em = com.dentalclinic.controllers.DatabaseController.getEntityManager();
        this.inventoryController = new InventoryController(em);
    }

    public void setWareHousePage(WareHousePage wareHousePage) {
        this.wareHousePage = wareHousePage;
    }

    @FXML
    public void handleAddInventory(){
        try{
            String name = addName.getText();
            double price = Double.parseDouble(addPrice.getText());
            int quantity = Integer.parseInt(addQuantity.getText());
            String supplier = addSupplier.getText();

            Inventory inventory = new Inventory();
            inventory.setItemName(name);
            inventory.setUnitPrice(price);
            inventory.setQuantity(quantity);
            inventory.setSupplier(supplier);

            inventoryController.addInventory(inventory);
            showAlert(Alert.AlertType.INFORMATION, "Thêm sản phẩm", "Thêm sản phẩm thành công");
            if(wareHousePage != null){
                wareHousePage.getInventoryTable().getItems().add(inventory);
            }
        }catch(NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá và số lượng phải hợp lệ");
        }


    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
