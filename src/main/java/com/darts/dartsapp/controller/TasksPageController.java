package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.*;
import com.darts.dartsapp.util.AITextToResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import com.darts.dartsapp.model.Class;


import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TasksPageController {

    @FXML public Button addButton;
    @FXML private VBox aiInputBox;
    @FXML private ComboBox<Assignments> AssignmentSelectionBox;
    @FXML private TextField AllocatedTimeField;
    @FXML private Button GenerateTasksButton;
    @FXML private VBox TaskList;
    @FXML private VBox addAssignmentBox;
    @FXML private ComboBox<Class> classSelectBox;
    @FXML private TextField assignmentTypeField;
    @FXML private TextField assignmentTimeField;
    @FXML private DatePicker assignmentDatePicker;
    @FXML private TextField assignmentWeightField;


    private final AssignmentsTable assignmentsTable = new AssignmentsTable();
    private final ClassTable classTable = new ClassTable();

    @FXML
    public void initialize() {
        List<Assignments> userAssignments = fetchAssignmentsForUser();      //load assignments that belong to logged in user (if any)
        ObservableList<Assignments> observableAssignments = FXCollections.observableArrayList(userAssignments);
        AssignmentSelectionBox.setItems(observableAssignments);

        //customised how assignment items are displayed in the dropdown menu
        Callback<ListView<Assignments>, ListCell<Assignments>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Assignments item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    com.darts.dartsapp.model.Class cls = classTable.getClass(item.getClassID());
                    String className = (cls != null) ? cls.getClassName() : "Unknown Class";
                    setText(className + " — " + item.getType());        //shows layout format as ClassName - Assignmenttype
                }
            }
        };

        AssignmentSelectionBox.setCellFactory(cellFactory);
        AssignmentSelectionBox.setButtonCell(cellFactory.call(null));
    }
    //returns assignments which the user have
    private List<Assignments> fetchAssignmentsForUser() {
        List<Assignments> allAssignments = new ArrayList<>();

        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            int userID = Session.getCurrentUser().getUserID();

            //gets all assignments for a user's classes
            List<com.darts.dartsapp.model.Class> userClasses = classTable.getClassesByUser(userID);

            for (com.darts.dartsapp.model.Class userClass : userClasses) {
                List<Assignments> classAssignments = assignmentsTable.getAssignmentsByClassID(userClass.getClassID());
                allAssignments.addAll(classAssignments);
            }
        } else {
            System.out.println("User not logged in, no assignments loaded.");       //error handling (stops crashes)
        }

        return allAssignments;
    }
    //generate Generate btn funtionality
    public void HandleGenerateTasks() {
        Assignments selectedAssignment = AssignmentSelectionBox.getValue();
        String allocatedTimeText = AllocatedTimeField.getText();

        if (selectedAssignment == null || allocatedTimeText == null || allocatedTimeText.isBlank()) {
            showError("Missing Input, Please select an assignment and enter the number of hours.");
            return;
        }

        int hours;
        try {
            hours = Integer.parseInt(allocatedTimeText);
            if (hours <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Invalid Input, Please enter a valid positive number for hours.");
            return;
        }

        ClassTable classTable = new ClassTable();
        com.darts.dartsapp.model.Class cls = classTable.getClass(selectedAssignment.getClassID());
        String className = cls.getClassName();

        String dueDay = selectedAssignment.getDay();
        String dueTime = selectedAssignment.getTime();          //gets assignment info
        int assignmentID = selectedAssignment.getAssignmentID();
            //Gives the AI the assignment info, generates and stores this into the tasks table (the db)
        AITextToResponse.generateAndStoreTasks(className, allocatedTimeText, dueDay, dueTime, assignmentID, hours);
        refreshTaskList(assignmentID);      //Refresh the task list for the assignment added so user sees new tasks
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Task Creation Failed");                 //Error message for error handling
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    //Refreshes to show the one assignment, for use when a new task/s is generated
    private void refreshTaskList(int assignmentID) {
        TaskList.getChildren().clear();
        TasksTable tasksTable = new TasksTable();                       //Refresh task list to display newly generated tasks
        if (Session.isLoggedIn() && Session.getCurrentUser() != null)
        {
          int userID = Session.getCurrentUser().getUserID();
          List<Tasks> tasks = tasksTable.getAllTasks(assignmentID); //gets all tasks for the user
            for (Tasks task : tasks) {
                CheckBox taskCheckBox = new CheckBox("• " + task.getDetails() + " (" + task.getDuration() + "h)"); //generates a new checkbox for each task, displaying task, detail and duration
                taskCheckBox.setStyle("-fx-font-size: 14px;");
                TaskList.getChildren().add(taskCheckBox);
            }
        }

    }

    //Same as above but refreshes ALL tasks for the user used by the REFRESH button
    private void refreshTaskList() {
        TaskList.getChildren().clear();
        TasksTable tasksTable = new TasksTable();

        //only gets tasks for loggedin users
        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            int userID = Session.getCurrentUser().getUserID();
            List<Tasks> tasks = tasksTable.getTasksByUserID(userID);    //get all tasks for a user by ID

            for (Tasks task : tasks) {
                CheckBox taskCheckBox = new CheckBox("• " + task.getDetails() + " (" + task.getDuration() + "h)"); //generates a new checkbox for each task, displaying task, detail and duration
                taskCheckBox.setStyle("-fx-font-size: 14px;");
                TaskList.getChildren().add(taskCheckBox);
            }
        }
    }
                //toggles the Add Task input screen
    @FXML
    private void OnAddButtonClick() {
        boolean showing = aiInputBox.isVisible();
        aiInputBox.setVisible(!showing);
        aiInputBox.setManaged(!showing);
    }

    @FXML
    private void OnRefreshButtonClick() {
        refreshTaskList();                      //refreshes tasks
    }


    @FXML
    private void onEditButtonClick() {
       // not complete
    }

    @FXML
    private void onDeleteButtonClick() {
        // not complete
    }

    @FXML
    private void onShowAddAssignmentClick() {
        boolean showing = addAssignmentBox.isVisible();
        addAssignmentBox.setVisible(!showing);              //Shows add assignment screen
        addAssignmentBox.setManaged(!showing);

        if (!showing && Session.isLoggedIn()) {     //only run if add assignment screen is shwon and user is logged in
            int userID = Session.getCurrentUser().getUserID();      //get user ID
            List<Class> userClasses = classTable.getClassesByUser(userID); //Fetch all classes belonging to said user
            classSelectBox.setItems(FXCollections.observableArrayList(userClasses));        //Show the combobox with user classes to allow them to select 1.
        }
    }

    @FXML
    private void onSaveAssignmentClick() {
        Class selectedClass = classSelectBox.getValue();
        String type = assignmentTypeField.getText();
        String time = assignmentTimeField.getText();                //fetch values entered by user into the form
        LocalDate date = assignmentDatePicker.getValue();
        String weightStr = assignmentWeightField.getText();

        if (selectedClass == null || type.isBlank() || time.isBlank() || date == null || weightStr.isBlank()) {     //check if entered values are incorrect (empty)
            showError("Please fill in all fields before saving.");
            return;
        }

        int weight;
        try {
            weight = Integer.parseInt(weightStr);           //attempt to translate weight (%) into int
        } catch (NumberFormatException e) {
            showError("Weight must be a valid number.");        //if invalid input detected, return
            return;
        }
        //creates new assignment
        Assignments newAssignment = new Assignments(
                selectedClass.getClassID(),     //class it belongs to
                time,                           //time due (i.e. 11.59pm)
                date.toString(),                //date due
                weight,                         // % weight of marks
                type,                           // assignment type
                "#2196F3" //the def. colour used for display
        );

        assignmentsTable.createAssignment(newAssignment);   //stores in DB
        AssignmentSelectionBox.getItems().add(newAssignment); //add to dropdown and autoselect
        AssignmentSelectionBox.setValue(newAssignment);

        addAssignmentBox.setVisible(false);
        addAssignmentBox.setManaged(false);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");                                      //confirm this was successful by sending alert
        alert.setHeaderText("Assignment added successfully!");
        alert.showAndWait();
    }


}
