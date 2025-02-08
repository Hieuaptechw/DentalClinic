package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.InventoryController;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


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


                String name = editName.getText().trim();
                String supplier = editSupplier.getText().trim();
                String  price = editPrice.getText();
                String quantity = editQuantity.getText();
                double unitPrice;
                int qty;
                if (name.isEmpty() || supplier.isEmpty()) {
                    messageLabel.setText("Tên sản phẩm và nhà cung cấp không được để trống");
                    messageLabel.setStyle("-fx-text-fill: red");
                    return;
                }else{
                    messageLabel.setText("");
                }

                try{
                    unitPrice = Double.parseDouble(price);
                    if(unitPrice < 0){
                        messageLabel.setText("Giá tiền không thể âm");
                        messageLabel.setStyle("-fx-text-fill: red");
                        return;
                    }else {
                        messageLabel.setText("");
                    }
                }catch(NumberFormatException e){
                        messageLabel.setText("Giá tiền phải là số hợp lệ");
                        messageLabel.setStyle("-fx-text-fill: red");
                        return;
                }

                try{
                    qty = Integer.parseInt(quantity);
                    if(qty < 0){
                        messageLabel.setText("Giá tiền không thể âm");
                        messageLabel.setStyle("-fx-text-fill: red");
                        return;
                    }else {
                        messageLabel.setText("");
                    }
                }catch(NumberFormatException e){
                    messageLabel.setText("Giá tiền phải là số hợp lệ");
                    messageLabel.setStyle("-fx-text-fill: red");
                    return;
                }

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
