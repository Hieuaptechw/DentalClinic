    package com.dentalclinic.views.pages.form;

    import com.dentalclinic.utils.AlertUtils;
    import com.dentalclinic.utils.DatabaseConnection;
    import com.dentalclinic.views.pages.manage.WareHousePage;
    import javafx.fxml.FXML;
    import javafx.scene.control.ComboBox;
    import javafx.scene.control.DatePicker;
    import javafx.scene.control.TextField;

    import java.sql.*;
    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    public class AddProductController {

        private WareHousePage wareHousePage;

        public void setWareHousePage(WareHousePage wareHousePage) {
            this.wareHousePage = wareHousePage;
        }

        @FXML
        private TextField nameField;

        @FXML
        private ComboBox<String> typeBox;

        @FXML
        private TextField priceField;

        @FXML
        private TextField quantityField;

        @FXML
        private DatePicker expiryDateField;


        String query = null;
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;


        @FXML
        public void initialize(){
            getCategory();
        }

        public void getCategory(){
            List<String> categoryNames = new ArrayList<>();
            try{
                connection = DatabaseConnection.getConnection();
                query = "SELECT name FROM category";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    categoryNames.add(resultSet.getString("name"));
                }
                typeBox.getItems().clear();
                typeBox.getItems().addAll(categoryNames);
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        public String randomCode(){
            Random random = new Random();
            char letter = (char) ('A' + random.nextInt(26));
            int number = random.nextInt(1000);
            String productCode = String.format("%c%03d", letter, number);
            return productCode;
        }

        public void addProduct(){
            String code = randomCode();
            String name = nameField.getText();
            String category = typeBox.getValue();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            LocalDate localDate = expiryDateField.getValue();
            Date expiryDate = (localDate != null) ? Date.valueOf(localDate) : null;

            try{
                connection = DatabaseConnection.getConnection();
                String categoryQuery = "SELECT id FROM category WHERE name = ?";
                preparedStatement = connection.prepareStatement(categoryQuery);
                preparedStatement.setString(1, category);
                resultSet = preparedStatement.executeQuery();

                int categoryId = 0;
                if(resultSet.next()){
                    categoryId = resultSet.getInt("id");
                }else{
                    return;
                }
                query = "INSERT INTO products (`code`,`productName`,`category_id`, `price`, `quantity`, `expiryDate`) VALUES (?,?,?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, code);
                preparedStatement.setString(2, name);
                preparedStatement.setInt(3, categoryId);
                preparedStatement.setDouble(4, price);
                preparedStatement.setInt(5, quantity);
                preparedStatement.setDate(6, expiryDate);
                preparedStatement.executeUpdate();
                AlertUtils.showSuccessAlert("Thêm sản phẩm thành công");

                if (wareHousePage != null) {
                    wareHousePage.loadData();
                } else {
                    System.out.println("Error: wareHousePage is not set.");
                }

            }catch(SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
