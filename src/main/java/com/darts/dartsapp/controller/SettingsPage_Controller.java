package com.darts.dartsapp.controller;

import com.darts.dartsapp.controller.DatabaseController;
import com.darts.dartsapp.model.Session;
import com.darts.dartsapp.model.User;
import com.darts.dartsapp.util.ThemeManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;

public class SettingsPage_Controller {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker birthdayPicker;

    @FXML private Label previewUsername;
    @FXML private Label previewPassword;
    @FXML private Label previewEmail;
    @FXML private Label previewPhone;
    @FXML private Label previewBirthday;
    @FXML private Label currentTimeLabel;

    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button editButton;
    @FXML private ToggleButton themeToggle;

    @FXML private VBox editFormSection;
    @FXML private HBox editActionButtons;

    @FXML private ComboBox<String> fontSizeDropdown;

    private final DatabaseController db = new DatabaseController();

    @FXML
    public void initialize() {
        fontSizeDropdown.setItems(FXCollections.observableArrayList("Small", "Medium", "Large"));           //populate font sizes into the dropdown
        fontSizeDropdown.setValue("Medium");
        fontSizeDropdown.setOnAction(e -> applyFontSize());

        User current = Session.getCurrentUser();
        if (current != null) {
            usernameField.setText(current.getUserName());
            passwordField.setText(current.getPassword());
            emailField.setText(current.getEmail());             //checks user is logged in, if so, show preview in preview box
            phoneField.setText(current.getPhoneNumber());
            birthdayPicker.setValue(current.getBirthday());
            updatePreview();
        }

        editFormSection.setVisible(false);
        editFormSection.setManaged(false);
        editActionButtons.setVisible(false);
        editActionButtons.setManaged(false);

        //apply theme
        Platform.runLater(() -> {
            Scene scene = themeToggle.getScene();
            if (scene != null) {
                ThemeManager.applyTheme(scene);
                themeToggle.setSelected(ThemeManager.isDarkMode());
                themeToggle.setText(ThemeManager.isDarkMode() ? "Dark" : "Light");
            }
        });
        //actions for the save del button
        saveButton.setOnAction(event -> saveChanges());
        deleteButton.setOnAction(event -> deleteAccount());
    }

    @FXML
    private void onEditClicked() {
        editFormSection.setVisible(true);
        editFormSection.setManaged(true);               //reveal the edit tabs when edit button is selected
        editActionButtons.setVisible(true);
        editActionButtons.setManaged(true);
    }

    @FXML
    private void saveChanges() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();                    //save new changes from input
        String phone = phoneField.getText();
        LocalDate birthday = birthdayPicker.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || birthday == null) {       //validate no empty fields
            showAlert("Missing Fields", "Please fill out all fields.");
            return;
        }
                //update and push changes to the DB
        User current = Session.getCurrentUser();
        if (current != null) {
            current.setUserName(username);
            current.setPassword(password);
            current.setEmail(email);
            current.setPhoneNumber(phone);

            db.getUsersTable().updateUser(current);
        }

        updatePreview();  //refresh preview
        showAlert("Updated", "Account info updated successfully.");

        editFormSection.setVisible(false);
        editFormSection.setManaged(false);
        editActionButtons.setVisible(false);
        editActionButtons.setManaged(false);
    }

    @FXML
    private void deleteAccount() {
        User current = Session.getCurrentUser();
        if (current == null) {
            showAlert("Error", "No logged-in user to delete.");         //checks if user is logged in
            return;
        }
        //attempt to delete user
        boolean success = db.getUsersTable().deleteUser(current.getUserID());

        if (success) {
            Session.clear();
            showAlert("Deleted", "Account deleted successfully. Returning to home.");

            try {
                Stage stage = (Stage) deleteButton.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/darts/dartsapp/LandingPage-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1324, 768);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to return to home.");
            }
        } else {
            showAlert("Not Found", "No account found with that ID.");
        }
    }

    @FXML
    private void toggleTheme() {
        boolean isDark = themeToggle.isSelected();
        themeToggle.setText(isDark ? "Dark" : "Light");
        ThemeManager.setDarkMode(isDark);                   //toggle between light and dark mode, applies theme via the ThemeManager
        Scene scene = themeToggle.getScene();
        if (scene != null) {
            ThemeManager.applyTheme(scene);
        }
    }

    @FXML
    private void setTime12() {
        currentTimeLabel.setText("Current Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));        //toggles 12hr time format
    }

    @FXML
    private void setTime24() {
        currentTimeLabel.setText("Current Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));          //toggle 24hr time format
    }

    private void applyFontSize() {
        String size = fontSizeDropdown.getValue();
        String css = switch (size) {
            case "Small" -> "-fx-font-size: 12px;";
            case "Large" -> "-fx-font-size: 20px;";                         //togles font size
            default -> "-fx-font-size: 16px;";
        };
        fontSizeDropdown.getScene().getRoot().setStyle(css);
    }

    private void updatePreview() {
        previewUsername.setText("Username: " + usernameField.getText());
        previewPassword.setText("Password: " + "*".repeat(passwordField.getText().length()));
        previewEmail.setText("Email: " + emailField.getText());                                         //preview of account info
        previewPhone.setText("Phone: " + phoneField.getText());
        previewBirthday.setText("Birthday: " +
                (birthdayPicker.getValue() != null ? birthdayPicker.getValue().toString() : ""));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);                                                          //alert message method
        alert.setContentText(message);
        alert.showAndWait();
    }
}
