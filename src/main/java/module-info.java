module com.example.test {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;

    exports com.dentalclinic;
    opens com.dentalclinic to javafx.fxml;
    opens com.dentalclinic.view to javafx.fxml;
}