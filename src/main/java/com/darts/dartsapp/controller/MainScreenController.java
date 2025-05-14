package com.darts.dartsapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainScreenController {

    @FXML private VBox Tasks;
    @FXML private VBox Calendar;

    @FXML
    protected void onTasksClick() {
        loadPage("/com/darts/dartsapp/TasksPage-view.fxml");
    }

    @FXML
    protected void onCalendarClick() {
        loadPage("/com/darts/dartsapp/calendar-view.fxml");
    }

    // Utility method to reduce duplication
    private void loadPage(String fxmlPath) {
        try {
            Stage stage = (Stage) Tasks.getScene().getWindow(); // Works for both Tasks and Calendar
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 1324, 768);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML: " + fxmlPath);
        }
    }
}
