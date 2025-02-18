package com.dentalclinic.views.pages.form;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.StaffController;
import com.dentalclinic.controllers.WorkSheduleController;
import com.dentalclinic.entities.Staff;
import com.dentalclinic.entities.WorkSchedule;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class WorkScheduleFormController {

    @FXML
    private TextField staffEmail;
    @FXML
    private DatePicker workingDay;
    @FXML
    private TextField startTime;
    @FXML
    private Label headerLabel;
    @FXML
    private TextField endTime;
    @FXML
    private Button actionButton;

    private StaffController staffController;
    private WorkSheduleController workSheduleController;
    private List<Staff> staffList;
    private boolean isEditMode = false;

    private WorkSchedule currentWorkSchedule;

    @FXML
    private void initialize() {
        EntityManager em = DatabaseController.getEntityManager();
        workSheduleController = new WorkSheduleController(em);
        staffController = new StaffController(em);
        staffList = staffController.getAllStaff();
    }
    public void setWorkSchedule(WorkSchedule workSchedule) {
        if (workSchedule != null) {
            this.currentWorkSchedule = workSchedule;
            staffEmail.setText(workSchedule.getStaff().getEmail());
            startTime.setText(workSchedule.getStartTime().toString());
            endTime.setText(workSchedule.getEndTime().toString());
            workingDay.setValue(workSchedule.getWorkingDay());
            headerLabel.setText("Edit Work Schedule");
            actionButton.setText("Update");
            isEditMode = true;
        }
    }

    public void setLabel() {
        headerLabel.setText("Add Work Schedule");
        actionButton.setText("Add");
        isEditMode = false;
    }

    @FXML
    public void handleSave() {
        String start = startTime.getText().trim();
        String end = endTime.getText().trim();
        String email = staffEmail.getText().trim();
        LocalDate workingDays = LocalDate.parse(workingDay.getValue().toString());

        if (start.isEmpty() || end.isEmpty() || email.isEmpty() || workingDays.isBefore(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ thông tin.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Optional<Staff> optionalStaff = staffList.stream()
                .filter(staff -> staff.getEmail().equalsIgnoreCase(email))
                .findFirst();

        if (optionalStaff.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không tìm thấy nhân viên với email: " + email, ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Staff selectedStaff = optionalStaff.get();
        LocalTime startLocalTime;
        LocalTime endLocalTime;

        try {
            startLocalTime = LocalTime.parse(start);
            endLocalTime = LocalTime.parse(end);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Định dạng thời gian không hợp lệ. Vui lòng sử dụng định dạng HH:mm.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (!startLocalTime.isBefore(endLocalTime)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Thời gian bắt đầu phải trước thời gian kết thúc.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        WorkSchedule workSchedule;
        if (isEditMode) {
            workSchedule = new WorkSchedule(
                    selectedStaff,
                    workingDays,
                    startLocalTime,
                    endLocalTime,
                    currentWorkSchedule.getCreatedAt(),
                    LocalDateTime.now()
            );
            workSchedule.setScheduleId(currentWorkSchedule.getScheduleId());
            workSheduleController.updateWorkSchedule(workSchedule);
            showAlert("Cập nhật thành công!");
        } else {
            workSchedule = new WorkSchedule(
                    selectedStaff,
                    workingDays,
                    startLocalTime,
                    endLocalTime,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            workSheduleController.addWorkSchedule(workSchedule);
            showAlert("Thêm lịch làm việc thành công!");
        }

        closeForm();
    }


    @FXML
    public void handleClear() {
        staffEmail.clear();
        startTime.clear();
        endTime.clear();
        currentWorkSchedule = null;
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void closeForm() {
        Stage stage = (Stage) staffEmail.getScene().getWindow();
        stage.close();
    }

}
