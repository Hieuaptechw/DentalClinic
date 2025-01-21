package com.dentalclinic;

import com.dentalclinic.utils.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DentalClinic extends Application {
    public static void main(String[] args) {
        testConnection();
        launch();
    }

    public static void testConnection(){
        try(Connection connection = DatabaseConnection.getConnection()){
            if(connection != null){
                System.out.println("Database connection successful!");
            }
        }catch(SQLException e){
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalClinic.class.getResource("views/primary.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}