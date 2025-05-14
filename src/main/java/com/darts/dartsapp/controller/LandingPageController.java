package com.darts.dartsapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.IOException;
import java.util.Objects;

public class LandingPageController {

    @FXML
    private Button SignUp;

    @FXML
    private Button Login;

    @FXML
    private ImageView imageView;

    @FXML
    public void initialize() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/com/darts/dartsapp/images/placeholder1.png");
            if (imageStream != null) {
                Image image = new Image(imageStream);
                imageView.setImage(image);
            } else {
                System.err.println("Image not found: /com/darts/dartsapp/images/placeholder1.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onButtonSignUpClick() {
        loadScene("/com/darts/dartsapp/SignUp-view.fxml");
    }

    @FXML
    protected void onButtonLoginClick() {
        loadScene("/com/darts/dartsapp/Login-view.fxml");
    }

    private void loadScene(String fxmlPath) {
        try {
            Stage stage = (Stage) SignUp.getScene().getWindow(); // works for both buttons
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML: " + fxmlPath);
        }
    }
}
