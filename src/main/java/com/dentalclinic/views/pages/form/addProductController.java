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

    public void setWareHousePage(WareHousePage warehousePage) {
        this.warehousePage = warehousePage;
    }


    private Connection connection;
    private PreparedStatement preparedStatement;



    @FXML
    public void initialize() {
        try {
            connection = DatabaseConnection.getConnection();
            typeBox.getItems().addAll(Category.values());
        } catch (SQLException e) {
            showError("Không thể kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    public void save() {
        try {
            if (nameField.getText().isEmpty() || priceField.getText().isEmpty() || quantityField.getText().isEmpty() || supplierField.getText().isEmpty()) {
                showError("Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            if (typeBox.getValue() == null) {
                showError("Vui lòng chọn loại sản phẩm!");
                return;
            }

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
                showInfo("Thêm sản phẩm thành công!");
                warehousePage.refreshTable();
                clean();
            }

        } catch (NumberFormatException e) {
            showError("Giá và số lượng phải là số!");
        } catch (SQLException e) {
            showError("Lỗi khi lưu sản phẩm: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void showInfo(String message) {
        System.out.println(message);
    }

    private void showError(String message) {
        System.out.println(message);
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

        code.append("P");

        for (int i = 0; i < 2; i++) {
            code.append(letters.charAt((int) (Math.random() * letters.length())));
        }
        
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
