module com.example.test {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires annotations;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    exports com.dentalclinic;
    opens com.dentalclinic to javafx.fxml;
    opens com.dentalclinic.views to javafx.fxml;
    opens com.dentalclinic.views.pages to javafx.fxml;
    opens com.dentalclinic.views.pages.manage to javafx.fxml;
    opens com.dentalclinic.views.pages.setting to javafx.fxml;
    opens com.dentalclinic.views.pages.administration to javafx.fxml;
    opens com.dentalclinic.entity to javafx.base;
    opens com.dentalclinic.views.pages.form to javafx.fxml;
}