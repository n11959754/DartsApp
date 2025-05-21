package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.*;
import com.darts.dartsapp.model.Class;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class MainScreenController {

    @FXML private VBox Tasks;
    @FXML private VBox Calendar;

    @FXML private VBox mondayPane, tuesdayPane, wednesdayPane, thursdayPane, fridayPane, saturdayPane, sundayPane;

    @FXML
    private void initialize() {
        loadWeeklyPreview();
    }

    @FXML
    protected void onTasksClick() {
        loadPage("/com/darts/dartsapp/TasksPage-view.fxml");
    }

    @FXML
    protected void onCalendarClick() {
        loadPage("/com/darts/dartsapp/calendar-view.fxml");
    }

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

        List<Class> userClasses = classTable.getClassesByUser(userID); // Must be implemented

        Map<String, List<Label>> dayContentMap = new HashMap<>();

        for (Class userClass : userClasses) {
            int classID = userClass.getClassID();
            String className = userClass.getClassName();

            List<ClassTimeSlot> timeSlots = timeSlotTable.getTimeSlotsByClassID(classID);
            for (ClassTimeSlot slot : timeSlots) {
                Label label = new Label(slot.getTime() + " - " + className + " (" + slot.getType() + ")");
                label.setMaxWidth(Double.MAX_VALUE);
                label.setStyle(
                                "-fx-background-color:" + slot.getColour() + ";" +
                                " -fx-text-fill: white;" +
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-background-radius: 8;"+
                                "-fx-font-weight: bold;"+
                                "fx-font-size: 14px;"
                );

                dayContentMap.computeIfAbsent(slot.getDay(), k -> new ArrayList<>()).add(label);
            }

            List<Assignments> assignments = assignmentsTable.getAssignmentsByClassID(classID);
            for (Assignments a : assignments) {
                Label label = new Label("[Due] " + a.getTime() + " - " + a.getType());
                label.setStyle("-fx-background-color:" + a.getColour() + "; -fx-text-fill: white; -fx-padding: 5;");
                dayContentMap.computeIfAbsent(a.getDay(), k -> new ArrayList<>()).add(label);
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
}
