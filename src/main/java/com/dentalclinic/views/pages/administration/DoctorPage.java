package com.dentalclinic.views.pages.administration;

import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;

@Page(name="Bác sĩ", icon="images/doctor.png",  fxml="administration/doctor.fxml")
public class DoctorPage extends AbstractPage {
    public DoctorPage() {
        System.out.println("DoctorPage initialized");
    }
}
