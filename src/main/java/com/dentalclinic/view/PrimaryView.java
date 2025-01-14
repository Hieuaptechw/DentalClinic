package com.dentalclinic.view;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PrimaryView {

    @FXML private TabPane toolbar;

    @FXML
    private void initialize() {
        for (Tab tab : toolbar.getTabs()) {
            for (Node item : ((HBox)tab.getContent()).getChildren()) {
                Label label = (Label)((VBox)item).getChildren().get(1);
                System.out.println(label.getText());
            }
        }
    }

//    private Node generateToolbarItem() {
//
//    }
}
