package ui.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;  // Đảm bảo nhập đúng lớp Scene của JavaFX
import javafx.stage.Stage;

import java.io.IOException;

public class DentalClinic extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/views/DashboardView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Quản Lý Phòng Khám Nha Khoa");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(); }
}
