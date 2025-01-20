package com.dentalclinic.views.pages.manage;

import com.dentalclinic.DentalClinic;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

@Page(name="Kho", icon="images/warehouse.png", fxml="manage/warehouse.fxml")
public class WareHousePage extends AbstractPage {

    @FXML
    public void loadFormInventory(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/manage/formProduct.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Inventory Management");
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
