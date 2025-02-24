package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.*;
import com.dentalclinic.validation.PatientValidator;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PatientFormController {

    @FXML
    private TextField nameField, emailField, phoneField, identityField;

    @FXML
    private ComboBox<PatientStatus> statusComboBox;

    @FXML
    private TextArea addressArea;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private RadioButton maleRadio,femaleRadio, otherRadio;

    @FXML
    private Button btnClear, btnAction;
    @FXML
    private Label lblTitle;

private PatientValidator patientValidator;
    private ToggleGroup genderGroup;
    private Patient patient, selectedPatient;
    private PatientController patientController;

    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        patientValidator = new PatientValidator();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        otherRadio.setToggleGroup(genderGroup);
        EntityManager em = DatabaseController.getEntityManager();
        patientController = new PatientController(em);
        loadPatientStatus();
    }


    @FXML
    private void handleAdd() {
        handleConfirmPatient();
    }

    private void loadPatientStatus() {
        statusComboBox.getItems().clear();
        statusComboBox.getItems().addAll(PatientStatus.values());
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
        identityField.setText(patient.getIdentityCard());
        statusComboBox.setValue(patient.getStatus());
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
            patientValidator.showAlert("Error", "Patient not found for update!");
            return;
        }
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        String identity = identityField.getText().trim();
        LocalDate dob = dobPicker.getValue();
        PatientStatus status = statusComboBox.getValue();
        if (name.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || status == null ||identity.isEmpty() || identityField.getText().isEmpty() || dob == null || address.isEmpty() ) {
            patientValidator.showAlert("Error", "All fields except specialty are required!");
            return;
        }
        Gender gender = null;
        if (maleRadio.isSelected()) {
            gender = Gender.MALE;
        } else if (femaleRadio.isSelected()) {
            gender = Gender.FEMALE;
        } else if (otherRadio.isSelected()) {
            gender = Gender.OTHER;
        }

        if (!patientValidator.isValidName(nameField.getText())) {
            patientValidator.showAlert("Error", "Name can only contain letters!");
            return;
        }
        if (!patientValidator.isValidEmail(emailField.getText())) {
            patientValidator.showAlert("Error", "Invalid email format!");
            return;
        }
        if (!patientValidator.isValidIndentityCard(identityField.getText())) {
            patientValidator.showAlert("Error", "Invalid id card format!");
            return;
        }
        if (!patientValidator.isValidPhone(phoneField.getText())) {
            patientValidator.showAlert("Error", "Invalid phone number!");
            return;
        }
        if (!patientValidator.isValidAddress(nameField.getText())) {
            patientValidator.showAlert("Error", "Name can only contain letters!");
            return;
        }
        if(patientController.checkIdentityCardExists(identityField.getText())) {
            patientValidator.showAlert("Error", "Identity Card already exists!");
        }


        patient.setName(name);
        patient.setEmail(email);
        patient.setPhone(phone);
        patient.setAddress(address);
        patient.setDob(dob);
        patient.setGender(gender);
        patient.setIdentityCard(identity);
        patient.setStatus(status);
        patient.setUpdatedAt(LocalDateTime.now());

        patientController.updatePatient(patient);
        patientValidator.showAlert("Success", "Patient updated successfully!");
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void handleConfirmPatient() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String identity = identityField.getText().trim();
        PatientStatus status = statusComboBox.getValue();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        LocalDate dob = dobPicker.getValue();
        Gender gender = getSelectedGender();
        if (name.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || status == null ||identity.isEmpty() || identityField.getText().isEmpty() || dob == null || address.isEmpty() ) {
            patientValidator.showAlert("Error", "All fields except specialty are required!");
            return;
        }

        if (!patientValidator.isValidName(nameField.getText())) {
            patientValidator.showAlert("Error", "Name can only contain letters!");
            return;
        }
        if (!patientValidator.isValidEmail(emailField.getText())) {
            patientValidator.showAlert("Error", "Invalid email format!");
            return;
        }
        if (!patientValidator.isValidIndentityCard(identityField.getText())) {
            patientValidator.showAlert("Error", "Invalid id card format!");
            return;
        }
        if (!patientValidator.isValidPhone(phoneField.getText())) {
            patientValidator.showAlert("Error", "Invalid phone number!");
            return;
        }
        if (!patientValidator.isValidAddress(addressArea.getText())) {
            patientValidator.showAlert("Error", "Invalid address format!");
            return;
        }
        if(patientController.checkIdentityCardExists(identityField.getText())) {
            patientValidator.showAlert("Error", "Identity Card already exists!");
        }
        selectedPatient = patientController.findPatientByIdentityCard(identity);
        if (!patientValidator.isValidBirthDay(dobPicker.getValue())) {
            patientValidator.showAlert("Error", "Invalid birth date! Must be older than 5 years.");
            return;
        }

        if (selectedPatient == null) {
            selectedPatient = new Patient();
            selectedPatient.setName(name);
            selectedPatient.setEmail(email);
            selectedPatient.setPhone(phone);
            selectedPatient.setIdentityCard(identity);
            selectedPatient.setAddress(address);
            selectedPatient.setDob(dob);
            selectedPatient.setGender(gender);
            selectedPatient.setStatus(status);
            selectedPatient.setCreatedAt(LocalDateTime.now());
            selectedPatient.setUpdatedAt(LocalDateTime.now());

            patientController.addPatient(selectedPatient);
            patientValidator.showAlert("Success", "Patient has been added!");
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.close();
        } else {
            patientValidator.showAlert("Error", "Patient already exists!");
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.close();
        }
    }


    private Gender getSelectedGender() {
        if (maleRadio.isSelected()) {
            return Gender.MALE;
        } else if (femaleRadio.isSelected()) {
            return Gender.FEMALE;
        } else {
            return Gender.OTHER;
        }
    }
    public Patient getPatient() {
        return selectedPatient;
    }
}
