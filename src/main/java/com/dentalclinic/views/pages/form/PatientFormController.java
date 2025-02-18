package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.*;
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


    private ToggleGroup genderGroup;
    private Patient patient, selectedPatient;
    private PatientController patientController;

    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        otherRadio.setToggleGroup(genderGroup);
        EntityManager em = DatabaseController.getEntityManager();
        patientController = new PatientController(em);
        setupComboBoxListeners();
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Patient not found for update!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        LocalDate dob = dobPicker.getValue();
        PatientStatus status = statusComboBox.getValue();

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
        patient.setStatus(status);
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
        String identity = identityField.getText().trim();
        PatientStatus status = statusComboBox.getValue();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        LocalDate dob = dobPicker.getValue();

        Gender gender = getSelectedGender();

        if (email.isEmpty() || phone.isEmpty() || name.isEmpty() || address.isEmpty() || dob == null || gender == null || status == null) {
            System.out.println("Please enter all required patient information!");
            return;
        }

        selectedPatient = patientController.findPatientByIdentityCard(identity);

        if (selectedPatient == null) {
            selectedPatient = new Patient();
            selectedPatient.setName(name);
            selectedPatient.setEmail(email);
            selectedPatient.setPhone(phone);
            selectedPatient.setIdentityCard(identity);
            selectedPatient.setAddress(address);
            selectedPatient.setDob(dob);
            selectedPatient.setGender(gender);
            selectedPatient.setStatus(status); // Gán trạng thái cho bệnh nhân mới
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

    private Gender getSelectedGender() {
        if (maleRadio.isSelected()) {
            return Gender.MALE;
        } else if (femaleRadio.isSelected()) {
            return Gender.FEMALE;
        } else {
            return Gender.OTHER;
        }
    }


    private void setupComboBoxListeners() {
        statusComboBox.setOnAction(event -> {
            PatientStatus patientStatus = statusComboBox.getSelectionModel().getSelectedItem();
            if (patientStatus != null) {
                System.out.println("Trạng thái được chọn : " + patientStatus);
            }
        });


    }
    public Patient getPatient() {
        return selectedPatient;
    }
}
