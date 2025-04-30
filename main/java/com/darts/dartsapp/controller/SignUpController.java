package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {
    @FXML
    private TextField UsernameField;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private TextField EmailField;

    @FXML
    private TextField PhoneField;

    @FXML
    private DatePicker BirthdayField;

    @FXML
    private Button SignUpButton;

    @FXML
    private Button LoginButton;

    @FXML
    private Button BackButton;

    private DatabaseController db;

    @FXML
    protected void onButtonLoginClick() throws IOException {
        Stage stage = (Stage) LoginButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/Login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }


    @FXML
    private void onBackButtonClick() throws IOException {
        db = new DatabaseController();
        User user = new User(UsernameField.getText(), EmailField.getText(), PhoneField.getText(), PasswordField.getText());
        db.usersTable().createUser(user);
        Stage stage = (Stage) BackButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/LandingPage-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

}
