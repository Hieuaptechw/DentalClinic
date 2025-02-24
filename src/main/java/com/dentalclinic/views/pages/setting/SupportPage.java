package com.dentalclinic.views.pages.setting;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;

import java.awt.Desktop;

import javafx.fxml.FXML;


@Page(name="Support", icon="images/customer-service.png", fxml="setting/support.fxml")
public class SupportPage extends AbstractPage {

    @FXML
    private void handleCallPhone() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("tel://555-1212-9876"));
    }

    @FXML
    private void handleSendEmail() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("mailto://support@dentalclinic.com"));
    }
}
