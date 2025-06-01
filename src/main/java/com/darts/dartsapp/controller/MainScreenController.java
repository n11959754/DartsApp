package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.*;
import com.darts.dartsapp.model.Class;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Cursor;



import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class MainScreenController {

    @FXML private VBox Tasks;
    @FXML private VBox Calendar;

    @FXML private VBox mondayPane, tuesdayPane, wednesdayPane, thursdayPane, fridayPane, saturdayPane, sundayPane;

    @FXML
    private void initialize() {
        loadWeeklyPreview();
        loadAssignmentCountdown(assignmentsCountdownVBox);          //load weeklypreview and countdown (sort of acts as a refresh button as new stuff will appear when reopening main)
    }

    @FXML
    protected void onTasksClick() {
        loadPage("/com/darts/dartsapp/TasksPage-view.fxml");        //takes user to tasks page when tasks widget is clicked via helper method
    }

    @FXML
    protected void onCalendarClick() {
        loadPage("/com/darts/dartsapp/calendar-view.fxml");     //takes user to calandar when cal widget is selected via helper method
    }

    @FXML private VBox assignmentsCountdownVBox;


    private void loadPage(String fxmlPath) {
        try {
            Stage stage = (Stage) Tasks.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 1324, 768);                      //helper method referenced to take user to different pages (prevented repeated code)
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML: " + fxmlPath);
        }
    }

    private void loadWeeklyPreview() {
        if (!Session.isLoggedIn()) return;              //exits user if they arent logged in

        int userID = Session.getCurrentUser().getUserID();

        ClassTable classTable = new ClassTable();
        AssignmentsTable assignmentsTable = new AssignmentsTable();             //sets up the access to db
        ClassTimeSlotTable timeSlotTable = new ClassTimeSlotTable();

        List<Class> userClasses = classTable.getClassesByUser(userID);          //retreives classes for the user logged in
        Map<String, List<Label>> dayContentMap = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);               //calculates the start end of week
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        for (Class userClass : userClasses) {
            int classID = userClass.getClassID();           //loop through all classes
            String className = userClass.getClassName();

            //fetch all the classes timeslots and convert them into styled labels
            List<ClassTimeSlot> timeSlots = timeSlotTable.getTimeSlotsByClassID(classID);
            for (ClassTimeSlot slot : timeSlots) {
                Label label = new Label(slot.getTime() + " - " + className + " (" + slot.getType() + ")");
                label.setWrapText(true);        //allows multiline
                label.setMaxWidth(Double.MAX_VALUE);        //fills the entire width of the container
                label.setCursor(Cursor.HAND);       //changes the cursor when the user hovers over the label
                //styleing for the label
                label.setStyle(
                        "-fx-background-color:" + slot.getColour() + ";" +      //makes the lable BG colour the same colour assigned when class was created (from the DB)
                                "-fx-text-fill: white;" +
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;"
                );
                label.setOnMouseEntered(e -> label.setStyle(
                        label.getStyle() +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0.5, 0, 2);" +
                                "-fx-scale-x: 1.02;" +                                                      //styling for when mouse hovers on calandar items
                                "-fx-scale-y: 1.02;"
                ));
                label.setOnMouseExited(e -> label.setStyle(
                        label.getStyle().replaceAll("-fx-effect: .*?;", "") +
                                "-fx-scale-x: 1.0;" +                                                       //styling when mouse leave calandar items
                                "-fx-scale-y: 1.0;"
                ));

                dayContentMap.computeIfAbsent(slot.getDay().toLowerCase(), k -> new ArrayList<>()).add(label);      //adds label to the correct day
            }


            //Assignments on the weekly preview
            List<Assignments> assignments = assignmentsTable.getAssignmentsByClassID(classID);
            for (Assignments a : assignments) {
                LocalDate dueDate = LocalDate.parse(a.getDay());
                if (dueDate.isBefore(startOfWeek) || dueDate.isAfter(endOfWeek)) continue; //skips assignments not due this week (before and after this week)

                String dayName = dueDate.getDayOfWeek().toString().toLowerCase();

                Label label = new Label("[Due] " + a.getTime() + " - " + a.getType());
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setCursor(Cursor.HAND);

                //styling for the assignments, purposfully made bigger to stand out more)
                label.setStyle(
                        "-fx-background-color:" + a.getColour() + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 16px;"
                );
                label.setOnMouseEntered(e -> label.setStyle(                                    //cursor hover effects
                        label.getStyle() +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0.5, 0, 2);" +
                                "-fx-scale-x: 1.02;" +
                                "-fx-scale-y: 1.02;"
                ));
                label.setOnMouseExited(e -> label.setStyle(
                        label.getStyle().replaceAll("-fx-effect: .*?;", "") +
                                "-fx-scale-x: 1.0;" +
                                "-fx-scale-y: 1.0;"
                ));

                dayContentMap.computeIfAbsent(dayName, k -> new ArrayList<>()).add(label);  //adds assignment to appropriate day due
            }
        }

        Comparator<Label> timeSorter = Comparator.comparing(l -> l.getText().split(" - ")[0]);      //sorts all events by time in acending order

        for (Map.Entry<String, List<Label>> entry : dayContentMap.entrySet()) {
            String day = entry.getKey();
            List<Label> labels = entry.getValue();
            labels.sort(timeSorter);        //sorts within the day

            Pane pane = getPaneForDay(day);
            if (pane != null) {
                pane.getChildren().clear(); //clears existing content incase of changes to classes / assignments
                pane.getChildren().addAll(labels);      //adds newly sorted labels (classes and assignments)
            }
        }
    }



    private Pane getPaneForDay(String day) {
        return switch (day.toLowerCase()) {
            case "monday" -> mondayPane;
            case "tuesday" -> tuesdayPane;
            case "wednesday" -> wednesdayPane;                      //panes for each day of the week which the class and assignments go in
            case "thursday" -> thursdayPane;
            case "friday" -> fridayPane;
            case "saturday" -> saturdayPane;
            case "sunday" -> sundayPane;
            default -> null;
        };
    }

    private void loadAssignmentCountdown(VBox container) {
        if (!Session.isLoggedIn()) return;              //ensures user is logged in

        int userID = Session.getCurrentUser().getUserID();          //gets id of curren user
        AssignmentsTable assignmentsTable = new AssignmentsTable();
        ClassTable classTable = new ClassTable();

        List<Assignments> allAssignments = new ArrayList<>();       //creates a list of all classes and assignments of user
        for (Class c : classTable.getClassesByUser(userID)) {       //loop through each class
            allAssignments.addAll(assignmentsTable.getAssignmentsByClassID(c.getClassID()));        //adds all assignments for this class to the assignments list
        }

        LocalDate today = LocalDate.now();


        allAssignments.removeIf(a -> LocalDate.parse(a.getDay()).isBefore(today)); //ensures only assignments comming up are shown, not past

        // Sort by soonest due
        allAssignments.sort(Comparator.comparing(a -> LocalDate.parse(a.getDay())));   //sorts by soonest to furtherest


        List<Assignments> top4 = allAssignments.stream().limit(4).toList();      //takes the top 4 of above as it will only display 4 assignments
        container.getChildren().clear();

        for (Assignments a : top4) {        //calculates days left for the 4 most recent assignments due
            LocalDate dueDate = LocalDate.parse(a.getDay());            //pars assignment due date
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);     //calculates days left

            String dueLabel = switch ((int) daysLeft) {
                case 0 -> "Today";
                case 1 -> "1 Day";                          //switches between Day, Today, and Days depending on how many day/s until due date
                default -> daysLeft + " Days";
            };
            //retreive the class name and assignment title
            String className = classTable.getClassNameByID(a.getClassID());
            String assignmentType = a.getType();
            String displayText = className + " — " + assignmentType;

            HBox row = new HBox(10);                //creates a HBOX for each, with spacing
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label name = new Label(displayText);        //Label showing class name and assignment + styling
            name.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 16px;");

            Region spacer = new Region();               //pushes the "due" label to the right
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            String colour = switch ((int) daysLeft) {       //Font colour depending on urgency
                case 0, 1, 2 -> "#FF5252";     //red, means urgent (due in or less than 3 days)
                case 3, 4, 5, 6, 7 -> "#FFEB3B"; //yellow, means semi-urgent (due in 4 to 7 days)
                default -> "#69F0AE";         //green, chill (due in greater than 7 days)
            };

            Label dueIn = new Label(dueLabel);          //label shows how many days left + styling
            dueIn.setStyle("-fx-text-fill: " + colour + "; -fx-font-size: 16px;");

            row.getChildren().addAll(name, spacer, dueIn);      //adds components to the row then to VBOX
            container.getChildren().add(row);
        }
    }



}
