package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.InventoryController;
import com.dentalclinic.entities.Inventory;
import com.dentalclinic.views.pages.manage.WareHousePage;
import jakarta.persistence.EntityManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class AddInventoryPage {
    @FXML
    private Label messageLabel;

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
            String name = addName.getText().trim();
            String price = addPrice.getText().trim();
            String quantity = addQuantity.getText().trim();
            String supplier = addSupplier.getText().trim();

            if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || supplier.isEmpty()) {
               messageLabel.setText("Vui lòng điền đầy đủ thông tin");
               messageLabel.setStyle("-fx-text-fill: red");
                return;
            }else {
                messageLabel.setText("");
            }

            double unitPrice;
            int qty;

            try{
                unitPrice = Double.parseDouble(price);
                if(unitPrice < 0){
                    messageLabel.setText("Lỗi nhập liệu giá tiền không thể âm");
                    messageLabel.setStyle("-fx-text-fill: red");
                    return;
                }else {
                    messageLabel.setText("");
                }
            }catch(NumberFormatException e){
                    messageLabel.setText("Đã có lỗi xảy ra");

                    return;
            }

            try{
                qty = Integer.parseInt(quantity);
                if(qty < 0){
                    messageLabel.setText("Lỗi nhập liệu số lượng không thể âm");
                    messageLabel.setStyle("-fx-text-fill: red");
                    return;
                }else {
                    messageLabel.setText("");
                }
            }catch(NumberFormatException e){
                messageLabel.setText("Đã có lỗi xảy ra");
                return;
            }


            Inventory inventory = new Inventory();
            inventory.setItemName(name);
            inventory.setUnitPrice(unitPrice);
            inventory.setQuantity(qty);
            inventory.setSupplier(supplier);

            inventoryController.addInventory(inventory);
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



}
