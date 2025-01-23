package com.dentalclinic.views.pages.manage;

import com.dentalclinic.entity.Category;
import com.dentalclinic.entity.Products;
import com.dentalclinic.entity.Status;
import com.dentalclinic.utils.AlertUtils;
import com.dentalclinic.utils.DatabaseConnection;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.EditProductController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    Products products = null;

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

            String sql = "DELETE FROM products WHERE code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, product.getCode());

            statement.executeUpdate();
            statement.close();
            connection.close();
            System.out.println("Đã xóa sản phẩm:");
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Xóa thất bại");
            alert.setContentText("Có lỗi xảy ra khi xóa sản phẩm: " + e.getMessage());
            alert.show();
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
                Status status = Status.fromString(statusString);
                Products product = new Products(code, productName, categoryName, price, quantity, expiryDate.toLocalDate(), status);
                productsObservableList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadFormAddProduct(){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/addProduct.fxml"));
                Parent parent = loader.load();
                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Thêm sản phẩm");
                stage.show();

        }catch(IOException e){
            e.printStackTrace();
        }
    }



}
