package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.InventoryController;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.views.pages.manage.WareHousePage;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class InventoryFormController {
    @FXML private TextField addName, addPrice, addQuantity, addSupplier;
    @FXML private TextField editName, editPrice, editQuantity, editSupplier;
    @FXML private Button btnAction, btnAction1;

    private WareHousePage wareHousePage;
    private Inventory inventory;
    private InventoryController inventoryController;

    public InventoryFormController() {
        EntityManager em = com.dentalclinic.controllers.DatabaseController.getEntityManager();
        this.inventoryController = new InventoryController(em);
    }
    public void setWareHousePage(WareHousePage wareHousePage) {
        this.wareHousePage = wareHousePage;
    }

    public void setInventoryData(Inventory inventory){
        this.inventory = inventory;
        editName.setText(inventory.getItemName());
        editPrice.setText(String.valueOf(inventory.getUnitPrice()));
        editQuantity.setText(String.valueOf(inventory.getQuantity()));
        editSupplier.setText(inventory.getSupplier());
    }


    @FXML
    private void handleSaveEditInventory(){
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
            closeWindow(btnAction);
            showAlert(Alert.AlertType.INFORMATION, "Cập nhật sản phẩm", "Sản phẩm đã được cập nhật");
        }
    }




    @FXML
    public void handleAddInventory(){
        try{
            String name = addName.getText().trim();
            Double price = Double.parseDouble(addPrice.getText().trim());
            int quantity = Integer.parseInt(addQuantity.getText().trim());
            String supplier = addSupplier.getText().trim();

            Inventory inventory = new Inventory();
            inventory.setItemName(name);
            inventory.setUnitPrice(price);
            inventory.setQuantity(quantity);
            inventory.setSupplier(supplier);

            inventoryController.addInventory(inventory);
            closeWindow(btnAction1);
            showAlert(Alert.AlertType.INFORMATION, "Thêm sản phẩm", "Thêm sản phẩm thành công");

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

    public void closeWindow(Button name){
        Stage stage = (Stage) name.getScene().getWindow();
        stage.close();
    }

}
