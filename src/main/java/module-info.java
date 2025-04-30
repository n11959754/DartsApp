module com.darts.dartsapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;


    opens com.darts.dartsapp to javafx.fxml;
    exports com.darts.dartsapp;
    exports com.darts.dartsapp.controller;
    opens com.darts.dartsapp.controller to javafx.fxml;
    exports com.darts.dartsapp.controller.calendar;
    opens com.darts.dartsapp.controller.calendar to javafx.fxml;
    exports com.darts.dartsapp.model;
    opens com.darts.dartsapp.model to javafx.fxml;

}