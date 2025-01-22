package com.dentalclinic.views.pages.manage;

import com.dentalclinic.entity.Category;
import com.dentalclinic.entity.Products;
import com.dentalclinic.entity.Status;
import com.dentalclinic.utils.DatabaseConnection;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TableColumn<Products, String> manufactureColumn;

    @FXML
    private TableColumn<Products, Double> totalColumn;





    @FXML
    private TableColumn<Products, String> actionColumn;

    ObservableList<Products> productsObservableList = FXCollections.observableArrayList();

    String query = null;
    PreparedStatement preparedStatement = null;
    Connection connection = null;
    ResultSet resultSet = null;
    Products products = null;

    @FXML
    private void initialize(){
        loadData();
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        expireColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        manufactureColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        inventoryTable.setItems(productsObservableList);
    }


    @FXML
    public void loadData() {
        productsObservableList.clear();

        try {
            connection = DatabaseConnection.getConnection();
            query = "SELECT p.id, p.code, p.productName, c.name AS categoryName, p.price, p.quantity, p.expiryDate, p.supplier, (p.price * p.quantity) AS totalPrice, c.status\n" +
                    "FROM products p\n" +
                    "JOIN category c ON p.category_id = c.id";

            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String code = resultSet.getString("code");
                String productName = resultSet.getString("productName");
                String categoryName = resultSet.getString("categoryName");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                java.sql.Date expiryDate = resultSet.getDate("expiryDate");
                String supplier = resultSet.getString("supplier");
                double totalPrice = resultSet.getDouble("totalPrice");
                String statusString = resultSet.getString("status");
                Status status = Status.fromString(statusString);

                Products product = new Products(code, productName, categoryName, price, quantity, expiryDate.toLocalDate(), supplier, totalPrice, status);
                productsObservableList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
