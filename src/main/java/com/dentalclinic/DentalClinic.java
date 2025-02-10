package com.dentalclinic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.dentalclinic.controllers.DatabaseController;

public class DentalClinic extends Application {
    private static Stage stage;
    public static Stage getStage() { return stage; }

    public static void main(String[] args) {
        DatabaseController.init();
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        DentalClinic.stage = stage;
        stage.setMinWidth(1200);
        stage.setMinHeight(600);
        stage.centerOnScreen();
        loadStage("views/login.fxml");
    }

    public static void loadStage(String path) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalClinic.class.getResource(path));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static void setTitle(String title) {
        stage.setTitle(title);
    }
}