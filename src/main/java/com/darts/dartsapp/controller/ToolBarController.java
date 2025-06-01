package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.Session;
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
            try {
                onSettingsClick();  // directly call your existing method
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });                                                     //actions for the context menu buttons

        signOut.setOnAction(e -> {
            try {
                Session.clear();  // Logs out the current user
                Stage stage = (Stage) profileButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/LandingPage-view.fxml"));
                Scene scene = new Scene(loader.load(), 1324, 768);
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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

    private void goTo(String fxmlPath) throws IOException{
        Stage stage = (Stage) MainScreen.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));           //helper method used when user is selected different pages, did it this way to prevent repeated code
        Scene scene = new Scene (loader.load(), 1324, 768);
        stage.setScene(scene);
    }

    // takes user to screens depending on what is clicked using goTo method
    @FXML
    protected void onMainScreenClick() throws IOException {
        goTo("/com/darts/dartsapp/MainScreen-view.fxml");
    }

    // takes user to settings
    @FXML
    protected void onSettingsClick() throws IOException {
        goTo("/com/darts/dartsapp/SettingsPage-view.fxml");
    }

    // takes user to calendar page
    @FXML
    protected void onCalendarClick() throws IOException {
        goTo("/com/darts/dartsapp/calendar-view.fxml");
    }
    //takes user to tasks page
    @FXML
    protected void onTasksClick() throws IOException {
        goTo("/com/darts/dartsapp/TasksPage-view.fxml");
    }
    @FXML
    protected void onCanvasClick() throws IOException {
        String url = "https://canvas.qut.edu.au/";                          //opens canvas in default browser
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
    }

    @FXML
    protected void onEmailClick() throws IOException {
        String url = "https://outlook.office365.com/mail/";                     //opens outlook on default browser.
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
    }
}
