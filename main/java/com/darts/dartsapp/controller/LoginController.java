package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField UsernameField;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private Button LoginButton;

    @FXML
    private Button FPButton;

    @FXML
    private Button SignUpButton;

    private DatabaseController db;

    @FXML
    protected void onButtonSignUpClick() throws IOException {

        db = new DatabaseController();
        User user = db.usersTable().getUser(UsernameField.getText());
        if (user.getPassword().equals(PasswordField.getText())) {
            Stage stage = (Stage) SignUpButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/SignUp-view.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
            stage.setScene(scene);
        }
        else {
            //send a message to the user saying the password or username was wrong
        }


    }

}
