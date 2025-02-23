module com.dentalclinic {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires annotations;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires mysql.connector.java;
    requires jbcrypt;


    exports com.dentalclinic;
    exports com.dentalclinic.controllers;
    opens com.dentalclinic.entities;
    opens com.dentalclinic to javafx.fxml;
    opens com.dentalclinic.views to javafx.fxml;
    opens com.dentalclinic.views.pages to javafx.fxml;
    opens com.dentalclinic.views.pages.manage to javafx.fxml;
    opens com.dentalclinic.views.pages.setting to javafx.fxml;
    opens com.dentalclinic.views.pages.administration to javafx.fxml;
    opens com.dentalclinic.views.pages.form to javafx.fxml;

}