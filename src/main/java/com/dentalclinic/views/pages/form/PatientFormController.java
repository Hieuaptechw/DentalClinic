package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.Gender;
import com.dentalclinic.entities.Patient;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientFormController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextArea addressArea;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private RadioButton maleRadio;

    @FXML
    private RadioButton femaleRadio;

    @FXML
    private RadioButton otherRadio;

    @FXML
    private Button btnClear;
    @FXML
    private Label lblTitle;
    @FXML
    private Button btnAction;

    private ToggleGroup genderGroup;
    private Patient patient;
    private PatientController patientController;
    private Patient selectedPatient;
    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        otherRadio.setToggleGroup(genderGroup);
        EntityManager em = DatabaseController.getEntityManager();
        patientController = new PatientController(em);
    }


    @FXML
    private void handleAdd() {
        handleConfirmPatient();

    }



    @FXML
    private void handleClear() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        addressArea.clear();
        dobPicker.setValue(null);
        genderGroup.selectToggle(null);
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        nameField.setText(patient.getName());
        emailField.setText(patient.getEmail());
        phoneField.setText(patient.getPhone());
        addressArea.setText(patient.getAddress());
        dobPicker.setValue(patient.getDob());
        if (patient.getGender() == Gender.MALE) {
            maleRadio.setSelected(true);
        } else if (patient.getGender() == Gender.FEMALE) {
            femaleRadio.setSelected(true);
        } else {
            otherRadio.setSelected(true);
        }
        btnAction.setText("Update");
        btnAction.getStyleClass().add("btn-update");
        btnAction.setOnAction(e->handleUpdate());
        lblTitle.setText("Edit Patient");
    }
    @FXML
    private void handleUpdate() {
        if (patient == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Patient not found for update!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        LocalDate dob = dobPicker.getValue();

        Gender gender = null;
        if (maleRadio.isSelected()) {
            gender = Gender.MALE;
        } else if (femaleRadio.isSelected()) {
            gender = Gender.FEMALE;
        } else if (otherRadio.isSelected()) {
            gender = Gender.OTHER;
        }

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || dob == null || gender == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all required information!", ButtonType.OK);
            alert.showAndWait();
            return;
        }


        patient.setName(name);
        patient.setEmail(email);
        patient.setPhone(phone);
        patient.setAddress(address);
        patient.setDob(dob);
        patient.setGender(gender);
        patient.setUpdatedAt(LocalDateTime.now());

        patientController.updatePatient(patient);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Patient updated successfully!", ButtonType.OK);
        alert.showAndWait();

        Stage stage = (Stage) btnAction.getScene().getWindow();
        stage.close();
    }

    private void handleConfirmPatient() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        LocalDate dob = dobPicker.getValue();

        Gender gender = null;
        if (maleRadio.isSelected()) {
            gender = Gender.MALE;
        } else if (femaleRadio.isSelected()) {
            gender = Gender.FEMALE;
        } else if (otherRadio.isSelected()) {
            gender = Gender.OTHER;
        }

        if (email.isEmpty() || phone.isEmpty() || name.isEmpty() || address.isEmpty() || dob == null || gender == null) {
            System.out.println("Please enter all required patient information!");
            return;
        }


        selectedPatient = patientController.findPatientByPhoneOrEmail(phone, email);

        if (selectedPatient == null) {
            selectedPatient = new Patient();
            selectedPatient.setName(name);
            selectedPatient.setEmail(email);
            selectedPatient.setPhone(phone);
            selectedPatient.setAddress(address);
            selectedPatient.setDob(dob);
            selectedPatient.setGender(gender);
            selectedPatient.setCreatedAt(LocalDateTime.now());
            selectedPatient.setUpdatedAt(LocalDateTime.now());

            patientController.addPatient(selectedPatient);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Patient has been added!", ButtonType.OK);
            alert.showAndWait();
            Stage stage = (Stage) btnAction.getScene().getWindow();
            stage.close();

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Patient already exists!", ButtonType.OK);
            alert.showAndWait();
            Stage stage = (Stage) btnAction.getScene().getWindow();
            stage.close();

        }
    }

    public Patient getPatient() {
        return selectedPatient;
    }
}
