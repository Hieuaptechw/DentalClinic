package com.dentalclinic.views.pages.form;

import com.dentalclinic.entity.Category;
import com.dentalclinic.utils.DatabaseConnection;
import com.dentalclinic.views.pages.manage.WareHousePage;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class addProductController {

    @FXML
    private CheckBox noExpiryCheckBox;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<Category> typeBox;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private DatePicker expiryDateField;

    @FXML
    private TextField supplierField;
    WareHousePage warehousePage = new WareHousePage();
    private Connection connection;
    private PreparedStatement preparedStatement;

    @FXML
    public void initialize() {
        try{
            connection = DatabaseConnection.getConnection();
            typeBox.getItems().addAll(Category.values());

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void save() {
        try {

            if (nameField.getText().isEmpty() || priceField.getText().isEmpty() || quantityField.getText().isEmpty() || supplierField.getText().isEmpty()) {
                System.out.println("Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            // Lấy dữ liệu từ các trường
            String code = generateRandomCode();
            String name = nameField.getText();
            Category category = typeBox.getValue();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            String supplier = supplierField.getText();
            Date expiryDate = noExpiryCheckBox.isSelected() || expiryDateField.getValue() == null
                    ? null
                    : Date.valueOf(expiryDateField.getValue());

            String query = "INSERT INTO product (code, product_name, category, price, quantity, expiry_date, supplier) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(query);


            preparedStatement.setString(1, code); // Code
            preparedStatement.setString(2, name); // Tên sản phẩm
            preparedStatement.setString(3, category.name()); // Loại sản phẩm
            preparedStatement.setDouble(4, price); // Giá
            preparedStatement.setInt(5, quantity); // Số lượng
            preparedStatement.setDate(6, expiryDate); // Ngày hết hạn
            preparedStatement.setString(7, supplier); // Nhà cung cấp


            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Thêm sản phẩm thành công!");
                clean();
            }

        } catch (NumberFormatException e) {
            System.out.println("Giá và số lượng phải là số!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    @FXML
    public void clean() {
        nameField.clear();
        typeBox.setValue(null);
        priceField.clear();
        quantityField.clear();
        expiryDateField.setValue(null);
        noExpiryCheckBox.setSelected(false);
        supplierField.clear();
    }

    private String generateRandomCode() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();

        // Bắt đầu với chữ 'P'
        code.append("P");

        // Random 2 chữ cái
        for (int i = 0; i < 2; i++) {
            code.append(letters.charAt((int) (Math.random() * letters.length())));
        }

        // Thêm dấu gạch ngang
        code.append("-");

        // Random 4 chữ số
        for (int i = 0; i < 4; i++) {
            code.append((int) (Math.random() * 10));
        }

        return code.toString();
    }

    private void closeResources() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
