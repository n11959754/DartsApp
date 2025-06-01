package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.Session;
import com.darts.dartsapp.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.darts.dartsapp.util.PasswordAuthentication;


import java.io.IOException;

public class LoginController {

    @FXML
    private TextField UsernameField;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private TextField VisiblePasswordField;

    @FXML
    private ToggleButton PasswordToggle;

    @FXML
    private Button LoginButton;

    @FXML
    private Button FPButton;

    @FXML
    private Button SignUpButton;

    private DatabaseController db;

    @FXML
    protected void onPasswordToggle() throws IOException {
        if (PasswordToggle.isSelected()) {      //if user selected show
            VisiblePasswordField.setText(PasswordField.getText());
            VisiblePasswordField.setVisible(true);
            VisiblePasswordField.setManaged(true);      //show the hidden password field
            PasswordField.setVisible(false);
            PasswordField.setManaged(false);
            PasswordToggle.setText("Hide");     //updates button to show Hide when password is shown
        } else {                                        //toggle between hidden and shown password
            PasswordField.setText(VisiblePasswordField.getText());      //else (user selected hide)
            PasswordField.setVisible(true);
            PasswordField.setManaged(true);                 //hides password
            VisiblePasswordField.setVisible(false);
            VisiblePasswordField.setManaged(false);
            PasswordToggle.setText("Show");             //makes button say show when password is hidden
        }
    }

    @FXML
    protected void onButtonSignUpClick() throws IOException {
        Stage stage = (Stage) SignUpButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/SignUp-view.fxml"));             //takes user to signup page

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");                     //method to display error message
        alert.setHeaderText(message);
        alert.showAndWait();
    }


    @FXML
    protected void onButtonLoginClick() throws IOException {

        db = new DatabaseController();
        User user = db.getUsersTable().getUser(UsernameField.getText());            //create db controller + retreive user by entered username

        if (user==null) {
            showError("Username or Password is incorrect.");            //error for when user doesnt exist
        }

        //compares stored hash password value to entered password
        String storedHash = user.getPassword();
        String enteredPassword = PasswordToggle.isSelected()
                ? VisiblePasswordField.getText()
                : PasswordField.getText();
        PasswordAuthentication auth = new PasswordAuthentication();
        boolean authenticated = auth.authenticate(enteredPassword.toCharArray(), storedHash);

        if (authenticated) {
            Session.setCurrentUser(user);
            Stage stage = (Stage) SignUpButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/MainScreen-view.fxml"));     //if correct, take user to mainscreen and "log them in" by making them the current user in the session
            Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
            stage.setScene(scene);
        }
        else {
            showError("Username or Password is incorrect.");        //passes error message to be displayed by showError method
        }


    }

}
