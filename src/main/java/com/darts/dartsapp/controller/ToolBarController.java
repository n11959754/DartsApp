package com.darts.dartsapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;




public class ToolBarController {

    // buttons to take user to different pages //
    @FXML private Button MainScreen;
    @FXML private Button Settings;
    @FXML private Button Calendar;
    @FXML private Button Tasks;

    @FXML private Button profileButton;
    private ContextMenu profileMenu;

    @FXML
    public void initialize(){    //Profile Pic in corner drop down menu (context menu)

        profileMenu = new ContextMenu();

        MenuItem settings = new MenuItem("Settings");
        MenuItem signOut = new MenuItem("Sign Out");


        settings.setOnAction(e -> {
            //open settings
        });                                                     //actions for the context menu buttons

        signOut.setOnAction(e -> {
            //sign out
        });

        profileMenu.getItems().addAll(settings, signOut);

        profileButton.setOnAction(e -> {
            if (profileMenu.isShowing()){
                profileMenu.hide();
            }                                                   //code to show / hide profile menu when clicked
            else {
                profileMenu.show(profileButton, Side.BOTTOM, 0, 0);
            }
        });
    }

    // takes user to main screen
    @FXML
    protected void onMainScreenClick() throws IOException {
        Stage stage = (Stage) MainScreen.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/MainScreen-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

    // takes user to settings
    @FXML
    protected void onSettingsClick() throws IOException {
        Stage stage = (Stage) Settings.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/SettingsPage-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

    // takes user to calendar page
    @FXML
    protected void onCalendarClick() throws IOException {
        Stage stage = (Stage) Calendar.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/calendar-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

    @FXML
    protected void onTasksClick() throws IOException {
        Stage stage = (Stage) Tasks.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/TasksPage-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }
    @FXML
    protected void onCanvasClick() throws IOException {
        String url = "https://canvas.qut.edu.au/";
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
    }

    @FXML
    protected void onEmailClick() throws IOException {
        String url = "https://outlook.office365.com/mail/";
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
    }
}
