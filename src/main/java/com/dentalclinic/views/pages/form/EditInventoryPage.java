package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.InventoryController;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.stage.Stage;


@Page(name="Sửa", icon="images/pen.png", fxml="form/EditInventory.fxml")
public class EditInventoryPage {
    @FXML
    private Label messageLabel;
    @FXML
    private TextField editName;

    @FXML
    private TextField editPrice;

    @FXML
    private TextField editQuantity;

    @FXML
    private TextField editSupplier;

    @FXML
    private Button btnAction;

    private Inventory inventory;
    private InventoryController inventoryController;


    public EditInventoryPage(){
        EntityManager em = DatabaseController.getEntityManager();
        this.inventoryController = new InventoryController(em);
    }

    public void setInventoryData(Inventory inventory){
        this.inventory = inventory;
        editName.setText(inventory.getItemName());
        editPrice.setText(String.valueOf(inventory.getUnitPrice()));
        editQuantity.setText(String.valueOf(inventory.getQuantity()));
        editSupplier.setText(inventory.getSupplier());
    }

    @FXML
    private void handleSave(){
        if(inventory != null){
                String name = editName.getText().trim();
                String supplier = editSupplier.getText().trim();
                String  price = editPrice.getText();
                String quantity = editQuantity.getText();

            inventory.setItemName(name);
            inventory.setUnitPrice(Double.parseDouble(price));
            inventory.setQuantity(Integer.parseInt(quantity));
            inventory.setSupplier(supplier);

            inventoryController.updateInventory(inventory);
            Stage stage = (Stage) btnAction.getScene().getWindow();
            stage.close();
           alertMessage(Alert.AlertType.INFORMATION, "Cập nhật sản phẩm", "Sản phẩm đã được cập nhật");
        }
    }

    public void alertMessage(Alert.AlertType type, String title, String headerText){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
}
