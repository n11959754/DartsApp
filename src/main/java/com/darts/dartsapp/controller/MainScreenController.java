package com.darts.dartsapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainScreenController {

@FXML private VBox Tasks;
@FXML private VBox Calendar;


    @FXML
    protected void onTasksClick() throws IOException {
        Stage stage = (Stage) Tasks.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/TasksPage-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

    @FXML
    protected void onCalendarClick() throws IOException {
        Stage stage = (Stage) Calendar.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/calendar-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

}
