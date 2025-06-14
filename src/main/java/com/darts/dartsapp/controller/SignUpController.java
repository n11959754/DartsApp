package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.darts.dartsapp.util.PasswordAuthentication;


import java.io.IOException;

public class SignUpController {
    @FXML
    private TextField UsernameField;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private TextField VisiblePasswordField;

    @FXML
    private ToggleButton PasswordToggle;

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
    protected void onPasswordToggle() throws IOException {
        if (PasswordToggle.isSelected()) {      //if user select show
            VisiblePasswordField.setText(PasswordField.getText());
            VisiblePasswordField.setVisible(true);
            VisiblePasswordField.setManaged(true);          //show the hidden password field
            PasswordField.setVisible(false);
            PasswordField.setManaged(false);
            PasswordToggle.setText("Hide");         //updates button to now say hide
        } else {                                    //toggle between hidden and shown passwords
            PasswordField.setText(VisiblePasswordField.getText());          //user selects hide
            PasswordField.setVisible(true);
            PasswordField.setManaged(true);                 //hides password field
            VisiblePasswordField.setVisible(false);
            VisiblePasswordField.setManaged(false);
            PasswordToggle.setText("Show");                 //changes button to say show when password is hidden
        }
    }

    public String ValidateSignUp(String username, String email, String phone, String password) {
        // if any fields are left empty
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            return "A field is not filled out.";
        }
        // if password is less than 10 characters
        if (password.length() < 10) {
            return "Passwords must be greater than 10 characters.";
        }
        // if email isnt valid
        if (!email.contains("@") || !email.contains(".")) {
            return "Entered email is invalid.";
        }
        // if phone isn't valid.
        if (phone.length() != 10) {
            return "Entered phone number is invalid.";
        }
        // if valid
        return null;
    }


    @FXML
    protected void onButtonLoginClick() throws IOException {
        Stage stage = (Stage) LoginButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/Login-view.fxml"));          //go to login view when login button is clicked

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }


    @FXML
    private void onBackButtonClick() throws IOException {
            //gets values from the form
        String username = UsernameField.getText();
        String email = EmailField.getText();
        String phone = PhoneField.getText();
        String rawPassword = PasswordField.isVisible() ? PasswordField.getText() : VisiblePasswordField.getText();
        String password = PasswordField.getText();
        //validates the values
        String SignUpError = ValidateSignUp(username, email, phone, password);

        if (SignUpError != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);         //if fails, show alert
            alert.setTitle("Account Creation Failed");
            alert.setHeaderText(SignUpError);
            alert.showAndWait();
            return;
        }
        //hash fucntion
        PasswordAuthentication auth = new PasswordAuthentication();
        String hashedPassword = auth.hash(rawPassword.toCharArray());

        //create new user and store in DB
        DatabaseController db = new DatabaseController();
        User user = new User(username, email, phone, hashedPassword);
        int userID = db.getUsersTable().createUser(user);
        db.getSettingsTable().createSettings(userID);

        //nav to landing page to allow user to now log in
        Stage stage = (Stage) BackButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/LandingPage-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
        stage.setScene(scene);
    }

}