package com.dentalclinic.views.pages.manage;

import com.dentalclinic.entity.Category;
import com.dentalclinic.entity.Products;
import com.dentalclinic.entity.Status;
import com.dentalclinic.utils.AlertUtils;
import com.dentalclinic.utils.DatabaseConnection;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.AddProductController;
import com.dentalclinic.views.pages.form.EditProductController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Page(name="Kho", icon="images/warehouse.png", fxml="manage/warehouse.fxml")
public class WareHousePage extends AbstractPage {
    @FXML
    private TableView<Products> inventoryTable;

    @FXML
    private TableColumn<Products, String> codeColumn;

    @FXML
    private TableColumn<Products, String> nameColumn;

    @FXML
    private TableColumn<Products, String> typeColumn;

    @FXML
    private TableColumn<Products, String> statusColumn;

    @FXML
    private TableColumn<Products, String> priceColumn;

    @FXML
    private TableColumn<Products, String> quantityColumn;

    @FXML
    private TableColumn<Products, String> expireColumn;

    @FXML
    private TableColumn<Products, Void> actionColumn;

    ObservableList<Products> productsObservableList = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null;
    Connection connection = null;
    ResultSet resultSet = null;

    @FXML
    private void initialize(){
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        expireColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        actionColumn.setCellFactory(param -> new TableCell<Products, Void>() {
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            {
                editButton.setOnAction(event -> {
                    Products product = getTableRow().getItem();
                    if (product != null) {
                        handleEdit(product);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Products product = getTableRow().getItem();
                    if (product != null) {
                        handleDelete(product);
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
                    box.setAlignment(Pos.CENTER);  // Căn giữa các nút trong HBox
                    setGraphic(box);

                }
            }
        });
        loadData();
        inventoryTable.setItems(productsObservableList);
    }

    private void handleDelete(Products product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Xóa sản phẩm");
        alert.setContentText("Bạn có chắc chắn muốn xóa sản phẩm: " + product.getProductName() + "?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteProductFromDatabase(product);
                productsObservableList.remove(product);
            }
        });
    }


    private void handleEdit(Products product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/editProductView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sửa Sản Phẩm");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteProductFromDatabase(Products product) {
        try {
            Connection connection = DatabaseConnection.getConnection();

           query = "DELETE FROM products WHERE code = ?";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, product.getCode());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            System.out.println("Đã xóa sản phẩm:");
        } catch (SQLException e) {
            e.printStackTrace();
           AlertUtils.showCustomAlert("Lỗi", "Xóa thất bại", Alert.AlertType.ERROR);
        }
    }



    @FXML
    public void loadData() {
        productsObservableList.clear();
        try {
            connection = DatabaseConnection.getConnection();
            query = "SELECT \n" +
                    "    products.code, \n" +
                    "    category.name AS categoryName, \n" +
                    "    products.productName, \n" +
                    "    products.price, \n" +
                    "    products.quantity, \n" +
                    "    products.expiryDate, \n" +
                    "    products.status \n" +
                    "FROM \n" +
                    "    products \n" +
                    "INNER JOIN \n" +
                    "    category \n" +
                    "ON \n" +
                    "    products.category_id = category.id;\n";

            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String code = resultSet.getString("code");
                String productName = resultSet.getString("productName");
                String categoryName = resultSet.getString("categoryName");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                java.sql.Date expiryDate = resultSet.getDate("expiryDate");
                String statusString = resultSet.getString("status");
                Status status = Status.valueOf(statusString.toUpperCase());

                LocalDate expiryLocalDate = null;
                if (expiryDate != null) {
                    expiryLocalDate = expiryDate.toLocalDate();
                } else {
                    expiryLocalDate = null;
                }

                Products product = new Products(code, productName, categoryName, price, quantity, expiryLocalDate, status);

                if (quantity < 5) {
                    product.setStatus(Status.OUT_OF_STOCK);
                    updateProductStatus(product);
                } else {
                    product.setStatus(Status.AVAILABLE);
                    updateProductStatus(product);
                }
                productsObservableList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateProductStatus(Products product) {
        try {
            query = "UPDATE products SET status = ? WHERE code = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, product.getStatus().name());
            preparedStatement.setString(2, product.getCode());
            preparedStatement.executeUpdate();
            System.out.println("Đã cập nhật trạng thái cho sản phẩm: " + product.getProductName());
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showCustomAlert("Lỗi", "Có lỗi xảy ra khi cập nhật trạng thái sản phẩm: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void loadFormAddProduct(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/addProduct.fxml"));
            Parent root = loader.load();
            AddProductController controller = loader.getController();
            controller.setWareHousePage(this);
            Stage stage = new Stage();
            stage.setTitle("Add Product");
            stage.setScene(new Scene(root));
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
