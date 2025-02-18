package com.dentalclinic.views.pages.administration;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.WorkSheduleController;
import com.dentalclinic.entities.WorkSchedule;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.WorkScheduleController;
import com.dentalclinic.views.pages.form.WorkScheduleFormController;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Page(name = "Lịch làm việc", icon = "images/working-hours.png", fxml = "administration/workschedule.fxml")
public class WorkSchedulePage extends AbstractPage {

    @FXML
    private GridPane calendarGrid;

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
        if (!calendarGrid.getStyleClass().contains("calendar-grid")) {
            calendarGrid.getStyleClass().add("calendar-grid");
        }
        String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int col = 0; col < 7; col++) {
            HBox headerBox = new HBox();
            headerBox.setAlignment(Pos.CENTER);
            headerBox.getStyleClass().add("calendar-header");

            Label dayHeaderLabel = new Label(weekDays[col]);
            dayHeaderLabel.getStyleClass().add("calendar-day-label");

            headerBox.getChildren().add(dayHeaderLabel);
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
                    cellBox.getChildren().add(dayLabel);
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
                    List<WorkSchedule> schedulesForDay = workSchedulesList.stream()
                            .filter(schedule -> schedule.getWorkingDay().equals(currentDate))
                            .toList();

                    VBox scheduleBox = new VBox();
                    int maxSchedulesToShow = 3;
                    for (int i = 0; i < Math.min(schedulesForDay.size(), maxSchedulesToShow); i++) {
                        WorkSchedule schedule = schedulesForDay.get(i);
                        Label scheduleLabel = new Label(
                                schedule.getStaff().getName() + ": " +
                                        schedule.getStartTime() + " - " +
                                        schedule.getEndTime()
                        );
                        scheduleLabel.getStyleClass().add("work-schedule-label");
                        scheduleBox.getChildren().add(scheduleLabel);
                    }
                    if (schedulesForDay.size() > maxSchedulesToShow) {
                        Label moreLabel = new Label("Xem thêm...");
                        moreLabel.getStyleClass().add("more-label");
                        moreLabel.setOnMouseClicked(event -> showEditSchedulePopup(currentDate, schedulesForDay));
                        scheduleBox.getChildren().add(moreLabel);
                    }
                    cellBox.getChildren().addAll(dayLabel, scheduleBox);
                    cellBox.setOnMouseClicked(event -> showEditSchedulePopup(currentDate, schedulesForDay));

                    dayCounter++;
                } else {
                    dayLabel.setText("");
                    cellBox.getChildren().add(dayLabel);
                }
                calendarGrid.add(cellBox, col, row);
            }
        }
    }
    @FXML
    public void handleAddSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/worksheduleform.fxml"));
            VBox root = (VBox) loader.load();
            WorkScheduleFormController workScheduleFormController = loader.getController();
            workScheduleFormController.setLabel();
            Stage stage = new Stage();
            stage.setTitle("Add Work Schedule");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadCalendar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEditSchedulePopup(LocalDate date, List<WorkSchedule> schedulesForDay) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/worksheduletable.fxml"));
            VBox root = (VBox) loader.load();
            WorkScheduleController workScheduleFormController = loader.getController();
            workScheduleFormController.setDate(date);
            workScheduleFormController.setSchedules(schedulesForDay);
            Stage stage = new Stage();
            stage.setTitle("Chi tiết lịch làm việc ngày " + date);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadCalendar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
