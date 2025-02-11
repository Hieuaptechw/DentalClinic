package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.InventoryController;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


@Page(name="Sửa", icon="images/pen.png", fxml="form/EditInventory.fxml")
public class EditInventoryPage {
    @FXML
    private TextField editName;

    @FXML
    private TextField editPrice;

    @FXML
    private TextField editQuantity;

    @FXML
    private TextField editSupplier;

    private Inventory inventory;
    private InventoryController inventoryController;
   private ObservableList<Inventory> inventories;

    public EditInventoryPage(){
        EntityManager em = com.dentalclinic.controllers.DatabaseController.getEntityManager();
        this.inventoryController = new InventoryController(em);
    }

    public void setInventoryData(Inventory inventory, ObservableList<Inventory> inventories){
        this.inventory = inventory;
        this.inventories = inventories;
        editName.setText(inventory.getItemName());
        editPrice.setText(String.valueOf(inventory.getUnitPrice()));
        editQuantity.setText(String.valueOf(inventory.getQuantity()));
        editSupplier.setText(inventory.getSupplier());
    }

    @FXML
    private void handleSave(){
        if(inventory != null){
            inventory.setItemName(editName.getText());
            inventory.setUnitPrice(Double.parseDouble(editPrice.getText()));
            inventory.setQuantity(Integer.parseInt(editQuantity.getText()));
            inventory.setSupplier(editSupplier.getText());
            inventoryController.updateInventory(inventory);

            for (int i = 0; i < inventories.size(); i++) {
                if (inventories.get(i).getInventoryId() == inventory.getInventoryId()) {
                    inventories.set(i, inventory);
                    break;
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Cập nhật thành công");
            alert.setContentText("Thông tin sản phẩm đã được cập nhật.");
            alert.showAndWait();
        }
    }




}
