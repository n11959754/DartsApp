package com.darts.dartsapp.controller;

import com.darts.dartsapp.model.*;
import com.darts.dartsapp.util.AITextToResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class TasksPageController {

    @FXML
    public Button addButton;

    @FXML
    private VBox aiInputBox;

    @FXML
    private ComboBox<Assignments> AssignmentSelectionBox;

    @FXML
    private TextField AllocatedTimeField;

    @FXML
    private Button GenerateTasksButton;

    @FXML
    private VBox TaskList;



    private final AssignmentsTable assignmentsTable = new AssignmentsTable();
    private final ClassTable classTable = new ClassTable();

    @FXML
    public void initialize() {
        List<Assignments> userAssignments = fetchAssignmentsForUser();
        ObservableList<Assignments> observableAssignments = FXCollections.observableArrayList(userAssignments);
        AssignmentSelectionBox.setItems(observableAssignments);


        Callback<ListView<Assignments>, ListCell<Assignments>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Assignments item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    com.darts.dartsapp.model.Class cls = classTable.getClass(item.getClassID());
                    setText(cls != null ? cls.getClassName() : "Unknown Class");
                }
            }
        };

        AssignmentSelectionBox.setCellFactory(cellFactory);
        AssignmentSelectionBox.setButtonCell(cellFactory.call(null));
    }

    private List<Assignments> fetchAssignmentsForUser() {
        List<Assignments> allAssignments = new ArrayList<>();

        if (Session.isLoggedIn() && Session.getCurrentUser() != null) {
            int userID = Session.getCurrentUser().getUserID();

            List<com.darts.dartsapp.model.Class> userClasses = classTable.getClassesByUser(userID);

            for (com.darts.dartsapp.model.Class userClass : userClasses) {
                List<Assignments> classAssignments = assignmentsTable.getAssignmentsByClassID(userClass.getClassID());
                allAssignments.addAll(classAssignments);
            }
        } else {
            System.out.println("User not logged in, no assignments loaded.");
        }

        return allAssignments;
    }

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
        String dueTime = selectedAssignment.getTime();
        int assignmentID = selectedAssignment.getAssignmentID();

        AITextToResponse.generateAndStoreTasks(className, allocatedTimeText, dueDay, dueTime, assignmentID, hours);
        refreshTaskList(assignmentID);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Task Creation Failed");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void refreshTaskList(int assignmentID) {
        TaskList.getChildren().clear();
        TasksTable tasksTable = new TasksTable();
        List<Tasks> tasks = tasksTable.getAllTasks(assignmentID);

        for (Tasks task : tasks) {
            Label taskLabel = new Label("• " + task.getDetails() + " (" + task.getDuration() + "h)");
            taskLabel.setStyle("-fx-font-size: 14px;");
            TaskList.getChildren().add(taskLabel);
        }
    }

    private void refreshTaskList() {
        TaskList.getChildren().clear();
        TasksTable tasksTable = new TasksTable();
        List<Tasks> tasks = tasksTable.getAllTasks();

        for (Tasks task : tasks) {
            Label taskLabel = new Label("• " + task.getDetails() + " (" + task.getDuration() + "h)");
            taskLabel.setStyle("-fx-font-size: 14px;");
            TaskList.getChildren().add(taskLabel);
        }
    }


    @FXML
    private void OnAddButtonClick() {
        boolean showing = aiInputBox.isVisible();
        aiInputBox.setVisible(!showing);
        aiInputBox.setManaged(!showing);
    }

    @FXML
    private void OnRefreshButtonClick() {
        refreshTaskList();
    }

    @FXML
    private void onSelectButtonClick() {
        // not complete
    }

    @FXML
    private void onEditButtonClick() {
       // not complete
    }

    @FXML
    private void onDeleteButtonClick() {
        // not complete
    }


}
