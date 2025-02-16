package com.dentalclinic.views.pages.administration;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.WorkSheduleController;
import com.dentalclinic.entities.WorkSchedule;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Page(name="Lịch làm việc", icon="images/working-hours.png", fxml="administration/workschedule.fxml")
public class WorkSchedulePage extends AbstractPage {
    @FXML private GridPane calendarGrid;
    private WorkSheduleController workSheduleController;

    @FXML
    private void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        workSheduleController = new WorkSheduleController(em);
        loadCalendar();
    }

    private void loadCalendar() {
        List<WorkSchedule> workSchedulesList = workSheduleController.getAllWorkSchedules();
        YearMonth currentMonth = YearMonth.now();
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int startDay = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonth.lengthOfMonth();

        calendarGrid.getChildren().clear();
        calendarGrid.getStyleClass().add("calendar-grid");

        // Tiêu đề ngày trong tuần
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int col = 0; col < 7; col++) {
            HBox headerBox = new HBox();
            headerBox.setAlignment(Pos.CENTER);
            headerBox.getStyleClass().add("calendar-header");

            Label dayLabel = new Label(days[col]);
            dayLabel.getStyleClass().add("calendar-day-label");

            if (col == 0) {
                dayLabel.getStyleClass().add("sunday"); // Chủ nhật màu đỏ
            }

            headerBox.getChildren().add(dayLabel);
            calendarGrid.add(headerBox, col, 0);
        }

        int dayCounter = 1;
        for (int row = 1; row <= 6; row++) {
            for (int col = 0; col < 7; col++) {
                VBox cellBox = new VBox();
                cellBox.setAlignment(Pos.TOP_LEFT);
                cellBox.getStyleClass().add("calendar-cell");
                cellBox.setPrefHeight(120);

                Label dayLabel = new Label();
                dayLabel.getStyleClass().add("calendar-day-number");

                if (row == 1 && col < startDay) {
                    dayLabel.setText("");
                } else if (dayCounter <= daysInMonth) {
                    LocalDate currentDate = currentMonth.atDay(dayCounter);
                    dayLabel.setText(String.valueOf(dayCounter));

                    if (col == 0) {
                        dayLabel.getStyleClass().add("sunday");
                    }

                    if (currentDate.equals(today)) {
                        cellBox.getStyleClass().add("today");
                        dayLabel.getStyleClass().add("today");
                    }

                    // Lấy danh sách lịch làm việc cho ngày này
                    List<WorkSchedule> schedulesForDay = workSchedulesList.stream()
                            .filter(schedule -> schedule.getWorkingDay().equals(currentDate))
                            .toList();

                    // Hiển thị lịch làm việc trong ô
                    int maxSchedulesToShow = 3;
                    VBox scheduleBox = new VBox();
                    for (int i = 0; i < Math.min(schedulesForDay.size(), maxSchedulesToShow); i++) {
                        WorkSchedule schedule = schedulesForDay.get(i);
                        Label scheduleLabel = new Label(schedule.getStaff().getName() + ": " +
                                schedule.getStartTime() + " - " + schedule.getEndTime());
                        scheduleLabel.getStyleClass().add("work-schedule-label");
                        scheduleBox.getChildren().add(scheduleLabel);
                    }

                    // Nếu có nhiều hơn maxSchedulesToShow lịch làm việc, thêm nút "Xem thêm"
                    if (schedulesForDay.size() > maxSchedulesToShow) {
                        Label moreLabel = new Label("Xem thêm...");
                        moreLabel.getStyleClass().add("more-label");
                        moreLabel.setOnMouseClicked(event -> showSchedulePopup(schedulesForDay));
                        scheduleBox.getChildren().add(moreLabel);
                    }

                    cellBox.getChildren().addAll(dayLabel, scheduleBox);
                    dayCounter++;

                    // Cho phép chỉnh sửa khi bấm vào ô ngày
                    cellBox.setOnMouseClicked(event -> showEditSchedulePopup(currentDate, schedulesForDay));
                } else {
                    dayLabel.setText("");
                }

                calendarGrid.add(cellBox, col, row);
            }
        }
    }
    private void showSchedulePopup(List<WorkSchedule> schedulesForDay) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Chi tiết lịch làm việc");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        for (WorkSchedule schedule : schedulesForDay) {
            Label scheduleLabel = new Label(schedule.getStaff().getName() + ": " +
                    schedule.getStartTime() + " - " + schedule.getEndTime());
            layout.getChildren().add(scheduleLabel);
        }

        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void showEditSchedulePopup(LocalDate date, List<WorkSchedule> schedulesForDay) {

    }

    private void openEditForm(WorkSchedule schedule) {

    }



}
