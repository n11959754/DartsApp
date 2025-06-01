package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.*;
import com.darts.dartsapp.model.Class;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Cursor;



import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class MainScreenController {

    @FXML private VBox Tasks;
    @FXML private VBox Calendar;

    @FXML private VBox mondayPane, tuesdayPane, wednesdayPane, thursdayPane, fridayPane, saturdayPane, sundayPane;

    @FXML
    private void initialize() {
        loadWeeklyPreview();
        loadAssignmentCountdown(assignmentsCountdownVBox);
    }

    @FXML
    protected void onTasksClick() {
        loadPage("/com/darts/dartsapp/TasksPage-view.fxml");
    }

    @FXML
    protected void onCalendarClick() {
        loadPage("/com/darts/dartsapp/calendar-view.fxml");
    }

    @FXML private VBox assignmentsCountdownVBox;


    private void loadPage(String fxmlPath) {
        try {
            Stage stage = (Stage) Tasks.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 1324, 768);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML: " + fxmlPath);
        }
    }

    private void loadWeeklyPreview() {
        if (!Session.isLoggedIn()) return;

        int userID = Session.getCurrentUser().getUserID();

        ClassTable classTable = new ClassTable();
        AssignmentsTable assignmentsTable = new AssignmentsTable();
        ClassTimeSlotTable timeSlotTable = new ClassTimeSlotTable();

        List<Class> userClasses = classTable.getClassesByUser(userID);
        Map<String, List<Label>> dayContentMap = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        for (Class userClass : userClasses) {
            int classID = userClass.getClassID();
            String className = userClass.getClassName();

            List<ClassTimeSlot> timeSlots = timeSlotTable.getTimeSlotsByClassID(classID);
            for (ClassTimeSlot slot : timeSlots) {
                Label label = new Label(slot.getTime() + " - " + className + " (" + slot.getType() + ")");
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setCursor(Cursor.HAND);
                label.setStyle(
                        "-fx-background-color:" + slot.getColour() + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;"
                );
                label.setOnMouseEntered(e -> label.setStyle(
                        label.getStyle() +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0.5, 0, 2);" +
                                "-fx-scale-x: 1.02;" +
                                "-fx-scale-y: 1.02;"
                ));
                label.setOnMouseExited(e -> label.setStyle(
                        label.getStyle().replaceAll("-fx-effect: .*?;", "") +
                                "-fx-scale-x: 1.0;" +
                                "-fx-scale-y: 1.0;"
                ));

                dayContentMap.computeIfAbsent(slot.getDay().toLowerCase(), k -> new ArrayList<>()).add(label);
            }

            List<Assignments> assignments = assignmentsTable.getAssignmentsByClassID(classID);
            for (Assignments a : assignments) {
                LocalDate dueDate = LocalDate.parse(a.getDay());
                if (dueDate.isBefore(startOfWeek) || dueDate.isAfter(endOfWeek)) continue;

                String dayName = dueDate.getDayOfWeek().toString().toLowerCase();

                Label label = new Label("[Due] " + a.getTime() + " - " + a.getType());
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setCursor(Cursor.HAND);
                label.setStyle(
                        "-fx-background-color:" + a.getColour() + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 16px;"
                );
                label.setOnMouseEntered(e -> label.setStyle(
                        label.getStyle() +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0.5, 0, 2);" +
                                "-fx-scale-x: 1.02;" +
                                "-fx-scale-y: 1.02;"
                ));
                label.setOnMouseExited(e -> label.setStyle(
                        label.getStyle().replaceAll("-fx-effect: .*?;", "") +
                                "-fx-scale-x: 1.0;" +
                                "-fx-scale-y: 1.0;"
                ));

                dayContentMap.computeIfAbsent(dayName, k -> new ArrayList<>()).add(label);
            }
        }

        Comparator<Label> timeSorter = Comparator.comparing(l -> l.getText().split(" - ")[0]);

        for (Map.Entry<String, List<Label>> entry : dayContentMap.entrySet()) {
            String day = entry.getKey();
            List<Label> labels = entry.getValue();
            labels.sort(timeSorter);

            Pane pane = getPaneForDay(day);
            if (pane != null) {
                pane.getChildren().clear();
                pane.getChildren().addAll(labels);
            }
        }
    }



    private Pane getPaneForDay(String day) {
        return switch (day.toLowerCase()) {
            case "monday" -> mondayPane;
            case "tuesday" -> tuesdayPane;
            case "wednesday" -> wednesdayPane;
            case "thursday" -> thursdayPane;
            case "friday" -> fridayPane;
            case "saturday" -> saturdayPane;
            case "sunday" -> sundayPane;
            default -> null;
        };
    }

    private void loadAssignmentCountdown(VBox container) {
        if (!Session.isLoggedIn()) return;

        int userID = Session.getCurrentUser().getUserID();
        AssignmentsTable assignmentsTable = new AssignmentsTable();
        ClassTable classTable = new ClassTable();

        List<Assignments> allAssignments = new ArrayList<>();
        for (Class c : classTable.getClassesByUser(userID)) {
            allAssignments.addAll(assignmentsTable.getAssignmentsByClassID(c.getClassID()));
        }

        LocalDate today = LocalDate.now();


        allAssignments.removeIf(a -> LocalDate.parse(a.getDay()).isBefore(today)); //ensures only assignments comming up are shown, not past

        // Sort by soonest due
        allAssignments.sort(Comparator.comparing(a -> LocalDate.parse(a.getDay())));   //sorts by soonest to furtherest


        List<Assignments> top4 = allAssignments.stream().limit(4).toList();      //takes the top 4 of above as it will only display 4 assignments
        container.getChildren().clear();

        for (Assignments a : top4) {
            LocalDate dueDate = LocalDate.parse(a.getDay());
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);

            String dueLabel = switch ((int) daysLeft) {
                case 0 -> "Today";
                case 1 -> "1 Day";
                default -> daysLeft + " Days";
            };

            String className = classTable.getClassNameByID(a.getClassID());
            String assignmentType = a.getType();
            String displayText = className + " — " + assignmentType;

            HBox row = new HBox(10);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label name = new Label(displayText);
            name.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 16px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            String colour = switch ((int) daysLeft) {
                case 0, 1, 2 -> "#FF5252";     //red, means urgent
                case 3, 4, 5, 6, 7 -> "#FFEB3B"; //yellow, means semi-urgent
                default -> "#69F0AE";         //green, chill
            };

            Label dueIn = new Label(dueLabel);
            dueIn.setStyle("-fx-text-fill: " + colour + "; -fx-font-size: 16px;");

            row.getChildren().addAll(name, spacer, dueIn);
            container.getChildren().add(row);
        }
    }



}
