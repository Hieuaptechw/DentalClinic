package com.dentalclinic.views.pages.manage;

import com.dentalclinic.DentalClinic;
import com.dentalclinic.entity.Category;
import com.dentalclinic.entity.Products;
import com.dentalclinic.entity.Status;
import com.dentalclinic.utils.DatabaseConnection;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.addProductController;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Page(name="Kho", icon="images/warehouse.png", fxml="manage/warehouse.fxml")
public class WareHousePage extends AbstractPage {

    @FXML
    private TableView<Products> inventoryTable;

    @FXML
    private TableColumn<Products, String>  codeColumn;

    @FXML
    private TableColumn<Products, String> nameColumn;

    @FXML
    private TableColumn<Products, String> typeColumn;

    @FXML
    private TableColumn<Products, Double> priceColumn;

    @FXML
    private TableColumn<Products, Integer> quantityColumn;

    @FXML
    private TableColumn<Products, LocalDate> expireColumn;

    @FXML
    private TableColumn<Products, Status> statusColumn;

    @FXML
    private TableColumn<Products, String> manufactureColumn;

    @FXML
    private TableColumn<Products, String> actionColumn;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    Products products = null;

    ObservableList<Products> productsObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        expireColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        manufactureColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        loadData();

    }

    @FXML
    private void loadData(){
        try {
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                System.out.println("Không thể kết nối đến cơ sở dữ liệu");
            } else {
                refreshTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void refreshTable(){
        productsObservableList.clear();
        try{
            query = "SELECT * FROM product";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String categoryString = resultSet.getString("category");
                Category category = Category.valueOf(categoryString.toUpperCase());
                productsObservableList.add(new Products(
                        resultSet.getString("code"),
                        resultSet.getString("product_name"),
                        category,
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getDate("expiry_date").toLocalDate(),
                        resultSet.getString("supplier")
                ));
                inventoryTable.setItems(productsObservableList);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void getAddView(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/addProduct.fxml"));
            Parent parent = loader.load();
            addProductController controller = loader.getController();
            controller.setWareHousePage(this);
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
