package com.dentalclinic.views;

import com.dentalclinic.DentalClinic;
import com.dentalclinic.entities.RoleType;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.entities.UserSession;
import com.dentalclinic.views.pages.administration.FinancePage;
import com.dentalclinic.views.pages.administration.RoomPage;
import com.dentalclinic.views.pages.administration.StaffPage;
import com.dentalclinic.views.pages.administration.WorkSchedulePage;
import com.dentalclinic.views.pages.manage.*;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.setting.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

public class PrimaryView {

    @FXML private TabPane toolbar;
    @FXML private VBox homeItemHolder;
    @FXML private HBox toolbarSettingTab;
    @FXML private HBox toolbarManageTab;
    @FXML private HBox toolbarAdministrationTab;
    @FXML private TabPane mainTabPane;

    @FXML private Tab administrationTab;

    @FXML
    private void initialize() {
        checkLogin();
        Staff user =UserSession.getCurrentUser();
        RoleType role = user.getRole();
        Node homeItem = generateToolbarPageItem(HomePage.class);
        homeItem.getStyleClass().add("home-item");
        System.out.println(role);
        homeItemHolder.getChildren().add(homeItem);
        if (role == RoleType.ADMIN) {
            toolbarManageTab.getChildren().addAll(
                    generateToolbarPageItem(PatientPage.class),
                    generateToolbarPageItem(ExaminationPage.class),
                    generateToolbarPageItem(AppointmentPage.class)
            );

            toolbarAdministrationTab.getChildren().addAll(
                    generateToolbarPageItem(StaffPage.class),
                    generateToolbarPageItem(WorkSchedulePage.class),
                    generateToolbarPageItem(FinancePage.class),
                    generateToolbarPageItem(RoomPage.class)
            );
            toolbarSettingTab.getChildren().addAll(
                    generateToolbarPageItem(SystemPage.class),
                    generateToolbarPageItem(BackupPage.class)
            );
        }

        if (role == RoleType.DOCTOR) {
            administrationTab.setDisable(true);
            toolbarManageTab.getChildren().addAll(
                    generateToolbarPageItem(PatientPage.class),
                    generateToolbarPageItem(ExaminationPage.class),
                    generateToolbarPageItem(AppointmentPage.class)
            );
            toolbarSettingTab.getChildren().addAll(
                    generateToolbarPageItem(SystemPage.class),
                    generateToolbarPageItem(BackupPage.class)
            );
        }
        if (role == RoleType.RECEPTIONIST) {
            homeItemHolder.setDisable(true);
            administrationTab.setDisable(true);
            toolbarManageTab.getChildren().addAll(
                    generateToolbarPageItem(PatientPage.class),
                    generateToolbarPageItem(ExaminationPage.class),
                    generateToolbarPageItem(AppointmentPage.class)


            );



            toolbarSettingTab.getChildren().addAll(
                    generateToolbarPageItem(SystemPage.class)

            );
        }
        Tab homeTab = openPage(HomePage.class);
        homeTab.setClosable(false);
    }
    @FXML
    private void handleLogout() {
        UserSession.clearSession();

        try {
            DentalClinic.loadStage("views/login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkLogin(){
        if (UserSession.getCurrentUser() == null) {
            try {
                DentalClinic.loadStage("views/login.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Nullable
    private Node generateToolbarPageItem(Class<?> target) {
        try {
            Page page = target.getAnnotation(Page.class);
            String name = page.name();
            String icon = page.icon();
            URL iconURL = DentalClinic.class.getResource(icon);
            if (iconURL == null) throw new IllegalStateException("Icon does not exist");

            VBox box = new VBox();
            box.setAlignment(Pos.CENTER);
            box.getStyleClass().add("toolbar-item");
            box.setOnMouseClicked((x) -> openPage(target));

            System.out.println(name + ": " + icon);
            Image image = new Image(iconURL.toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(32);
            imageView.setFitHeight(32);
            imageView.getStyleClass().add("toolbar-item-image");

            Label label = new Label(name);
            label.getStyleClass().add("toolbar-item-label");

            box.getChildren().addAll(imageView, label);

            return box;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Tab openPage(Class<?> target) {
        try {
            Page page = target.getAnnotation(Page.class);
            String name = page.name();
            String fxml = page.fxml();
            URL fxmlURL = Page.class.getResource(fxml);
            if (fxmlURL == null) throw new IllegalStateException("FXML file does not exist");

            Tab tab;
            ObservableList<Tab> tabs = mainTabPane.getTabs();
            if (page.allowsDuplicates() || (tab = tabs.stream().filter(x -> x.getUserData() == page).findAny().orElse(null)) == null) {
                tab = new Tab(name);
                tab.setUserData(page);
                tab.setContent(FXMLLoader.load(fxmlURL));
                tabs.add(tab);
            }
            mainTabPane.getSelectionModel().select(tab);

            return tab;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
