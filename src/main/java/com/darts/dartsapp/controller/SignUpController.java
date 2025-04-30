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
import javafx.scene.control.Alert;
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

    @FXML
    protected void onButtonLoginClick() throws IOException {
        Stage stage = (Stage) LoginButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/Login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }


    @FXML
    private void onBackButtonClick() throws IOException {

        String username = UsernameField.getText();
        String email = EmailField.getText();
        String phone = PhoneField.getText();
        String password = PasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || BirthdayField.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Account Creation Failed");
            alert.setHeaderText("A field is not filled out.");
            alert.showAndWait();
            return;
        }
        if (password.length() < 10) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Account Creation Failed");
            alert.setHeaderText("Entered password is invalid.");
            alert.setContentText("Passwords must be greater than 10 characters");
            alert.showAndWait();
            return;
        }
        if (!email.contains(".") || !email.contains("@")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Account Creation Failed");
            alert.setHeaderText("Entered email is invalid.");
            alert.showAndWait();
            return;
        }
        if (phone.length() != 10) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Account Creation Failed");
            alert.setHeaderText("Entered phone number is invalid.");
            alert.showAndWait();
            return;
        }


        DatabaseController db = new DatabaseController();
        User user = new User(UsernameField.getText(), EmailField.getText(), PhoneField.getText(), PasswordField.getText());
        db.usersTable().createUser(user);
        Stage stage = (Stage) BackButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/LandingPage-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

}