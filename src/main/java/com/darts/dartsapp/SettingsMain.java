package com.darts.dartsapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SettingsMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/darts/dartsapp/SettingsPage-view.fxml"));
        primaryStage.setTitle("Settings Page");
        primaryStage.setScene(new Scene(root, 1324, 768));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
