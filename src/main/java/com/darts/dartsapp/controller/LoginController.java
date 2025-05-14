package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.Session;
import com.darts.dartsapp.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
        if (PasswordToggle.isSelected()) {
            VisiblePasswordField.setText(PasswordField.getText());
            VisiblePasswordField.setVisible(true);
            VisiblePasswordField.setManaged(true);
            PasswordField.setVisible(false);
            PasswordField.setManaged(false);
            PasswordToggle.setText("Hide");
        } else {
            PasswordField.setText(VisiblePasswordField.getText());
            PasswordField.setVisible(true);
            PasswordField.setManaged(true);
            VisiblePasswordField.setVisible(false);
            VisiblePasswordField.setManaged(false);
            PasswordToggle.setText("Show");
        }
    }
    //.
    @FXML
    protected void onButtonSignUpClick() throws IOException {
        Stage stage = (Stage) SignUpButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/SignUp-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

    @FXML
    protected void onButtonLoginClick() throws IOException {

        db = new DatabaseController();
        User user = db.getUsersTable().getUser(UsernameField.getText());
        if (user != null && user.getPassword().equals(PasswordField.getText())) {
            Session.setCurrentUser(user);
            Stage stage = (Stage) SignUpButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/MainScreen-view.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
            stage.setScene(scene);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Username or Password is incorrect.");
            alert.showAndWait();
        }


    }

}
