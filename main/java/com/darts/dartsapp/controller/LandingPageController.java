package com.darts.dartsapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import java.io.IOException;

public class LandingPageController {

    @FXML
    private Button SignUp;

    @FXML
    private Button Login;

    @FXML
    private ImageView imageView;

    public void initialize() {
        // Load the image from the resources folder
        Image image = new Image(getClass().getResourceAsStream("/images/placeholder1.png"));

        // Set the loaded image to the ImageView
        imageView.setImage(image);

    }


    @FXML
    protected void onButtonSignUpClick() throws IOException {
        Stage stage = (Stage) SignUp.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/SignUp-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }


    @FXML
    protected void onButtonLoginClick() throws IOException {
        Stage stage = (Stage) Login.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

}
