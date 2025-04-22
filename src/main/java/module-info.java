module com.darts.dartsapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.darts.dartsapp to javafx.fxml;
    exports com.darts.dartsapp;
    exports com.darts.dartsapp.controller;
    opens com.darts.dartsapp.controller to javafx.fxml;
}